package com.wicom.watcher.hive;

import backtype.storm.task.IMetricsContext;
import backtype.storm.topology.FailedException;
import com.esotericsoftware.minlog.Log;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wicom.watcher.GenericConstants;
import com.wicom.watcher.audit.HbaseAudit;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.storm.hive.common.HiveOptions;
import org.apache.storm.hive.common.HiveUtils;
import org.apache.storm.hive.common.HiveWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.state.State;
import storm.trident.tuple.TridentTuple;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by albert.elkhoury on 3/23/2015.
 */
public class HiveStateCDR implements State {
    private static final Logger LOG = LoggerFactory.getLogger(HiveStateCDR.class);
    private HiveOptions options;
    private Integer currentBatchSize;
    private ExecutorService callTimeoutPool;
    private transient Timer heartBeatTimer;
    private AtomicBoolean timeToSendHeartBeat = new AtomicBoolean(false);
    private UserGroupInformation ugi =UserGroupInformation.createRemoteUser("sm_user");
    private Boolean kerberosEnabled = Boolean.valueOf(false);
    HashMap<HiveEndPoint, HiveWriter> allWriters;
    private String auditDbAuditTable;
    private Integer rowCount;
    private Integer batchCount;

    public HiveStateCDR (HiveOptions options) {
        this.options=options;
        this.currentBatchSize = Integer.valueOf(0);
        this.rowCount =  Integer.valueOf(0);
        this.batchCount =  Integer.valueOf(0);
    }

    public void beginCommit(Long txId) {
    }

    public void commit(Long txId) {
    }

    public void prepare(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
        try {
            if(this.options.getKerberosPrincipal() == null && this.options.getKerberosKeytab() == null) {
                this.kerberosEnabled = Boolean.valueOf(false);
            } else {
                if(this.options.getKerberosPrincipal() == null || this.options.getKerberosKeytab() == null) {
                    throw new IllegalArgumentException("To enable Kerberos, need to set both KerberosPrincipal  & KerberosKeytab");
                }

                this.kerberosEnabled = Boolean.valueOf(true);
            }

            if(this.kerberosEnabled.booleanValue()) {
                try {
                    this.ugi = HiveUtils.authenticate(this.options.getKerberosKeytab(), this.options.getKerberosPrincipal());
                } catch (HiveUtils.AuthenticationFailed var6) {
                    LOG.error("Hive kerberos authentication failed " + var6.getMessage(), var6);
                    throw new IllegalArgumentException(var6);
                }
            }
            this.auditDbAuditTable= (String) conf.get(GenericConstants.AUDIT_DB_AUDITTABLE);
            this.allWriters = new HashMap();
            String e = "hive-bolt-%d";
            this.callTimeoutPool = Executors.newFixedThreadPool(1, (new ThreadFactoryBuilder()).setNameFormat(e).build());
            this.heartBeatTimer = new Timer();
            this.setupHeartBeatTimer();
        } catch (Exception var7) {
            LOG.warn("unable to make connection to hive ", var7);
        }

    }

    public void updateState(List<TridentTuple> tuples, TridentCollector collector) {
        try {
            LOG.warn("Calling writeTuples method");
            this.writeTuples(tuples);
        } catch (Exception var4) {
            this.abortAndCloseWriters();
            LOG.warn("hive streaming failed.", var4);
            throw new FailedException(var4);
        }
    }

    private void writeTuples(List<TridentTuple> tuples) throws Exception {
        if(this.timeToSendHeartBeat.compareAndSet(true, false)) {
            this.enableHeartBeatOnAllWriters();
        }

        Iterator i$ = tuples.iterator();

        while(i$.hasNext()) {
            TridentTuple tuple = (TridentTuple)i$.next();
            String lastFieldValue=tuple.getValue(tuple.getFields().toList().size()-1).toString();
            if(lastFieldValue!=null) {
                if (lastFieldValue.equalsIgnoreCase("File End")) {
                    LOG.warn("Reaching File End : " + tuple.getValue(1).toString());
                    this.flushAllWriters();
                    LOG.warn("Writing Batch #{} with {} rows to Hive metastore.",batchCount,currentBatchSize);
                    this.currentBatchSize = Integer.valueOf(0);
                    this.batchCount++;
                    try {
                        HbaseAudit hba = new HbaseAudit(auditDbAuditTable);
                        hba.updateAudit(tuple.getValue(0).toString(), String.valueOf(this.rowCount));
                        this.rowCount=Integer.valueOf(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    List partitionVals = this.options.getMapper().mapPartitions(tuple);
                    HiveEndPoint endPoint = HiveUtils.makeEndPoint(partitionVals, this.options);
                    HiveWriter writer = this.getOrCreateWriter(endPoint);
                    writer.write(this.options.getMapper().mapRecord(tuple));

                    Integer var7 = this.currentBatchSize;
                    Integer var8 = this.currentBatchSize = Integer.valueOf(this.currentBatchSize.intValue() + 1);
                    this.rowCount++;//count the rows for each file
                    if (this.currentBatchSize.intValue() >= this.options.getBatchSize().intValue()) {
                        this.flushAllWriters();
                        LOG.warn("Writing Batch #{} with {} rows to Hive metastore.",batchCount,currentBatchSize);
                        this.currentBatchSize = Integer.valueOf(0);
                        this.batchCount++;
                    }

                }
            }
        }

    }

    private void abortAndCloseWriters() {
        try {
            this.abortAllWriters();
            this.closeAllWriters();
        } catch (InterruptedException var2) {
            LOG.warn("unable to close hive connections. ", var2);
        } catch (IOException var3) {
            LOG.warn("unable to close hive connections. ", var3);
        }

    }

    private void abortAllWriters() throws InterruptedException {
        Iterator i$ = this.allWriters.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry entry = (Map.Entry)i$.next();
            ((HiveWriter)entry.getValue()).abort();
        }

    }

    private void closeAllWriters() throws InterruptedException, IOException {
        Iterator i$ = this.allWriters.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry entry = (Map.Entry)i$.next();
            ((HiveWriter)entry.getValue()).close();
        }

        this.allWriters.clear();
    }

    private void setupHeartBeatTimer() {
        if(this.options.getHeartBeatInterval().intValue() > 0) {
            this.heartBeatTimer.schedule(new TimerTask() {
                public void run() {
                    HiveStateCDR.this.timeToSendHeartBeat.set(true);
                    HiveStateCDR.this.setupHeartBeatTimer();
                }
            }, (long)(this.options.getHeartBeatInterval().intValue() * 1000));
        }

    }

    private void flushAllWriters() throws HiveWriter.CommitFailure, HiveWriter.TxnBatchFailure, HiveWriter.TxnFailure, InterruptedException {
        Iterator i$ = this.allWriters.values().iterator();

        while(i$.hasNext()) {
            HiveWriter writer = (HiveWriter)i$.next();
            writer.flush(true);
        }

    }

    private void enableHeartBeatOnAllWriters() {
        Iterator i$ = this.allWriters.values().iterator();

        while(i$.hasNext()) {
            HiveWriter writer = (HiveWriter)i$.next();
            writer.setHeartBeatNeeded();
        }

    }

    private HiveWriter getOrCreateWriter(HiveEndPoint endPoint) throws HiveWriter.ConnectFailure, InterruptedException {
        try {
            HiveWriter e = (HiveWriter)this.allWriters.get(endPoint);
            if(e == null) {
                LOG.info("Creating Writer to Hive end point : " + endPoint);
                e = HiveUtils.makeHiveWriter(endPoint, this.callTimeoutPool, this.ugi, this.options);
                if(this.allWriters.size() > this.options.getMaxOpenConnections().intValue()) {
                    int retired = this.retireIdleWriters();
                    if(retired == 0) {
                        this.retireEldestWriter();
                    }
                }

                this.allWriters.put(endPoint, e);
            }

            return e;
        } catch (HiveWriter.ConnectFailure var4) {
            LOG.error("Failed to create HiveWriter for endpoint: " + endPoint, var4);
            throw var4;
        }
    }

    private void retireEldestWriter() {
        long oldestTimeStamp = System.currentTimeMillis();
        HiveEndPoint eldest = null;
        Iterator e = this.allWriters.entrySet().iterator();

        while(e.hasNext()) {
            Map.Entry entry = (Map.Entry)e.next();
            if(((HiveWriter)entry.getValue()).getLastUsed() < oldestTimeStamp) {
                eldest = (HiveEndPoint)entry.getKey();
                oldestTimeStamp = ((HiveWriter)entry.getValue()).getLastUsed();
            }
        }

        try {
            LOG.info("Closing least used Writer to Hive end point : " + eldest);
            ((HiveWriter)this.allWriters.remove(eldest)).close();
        } catch (IOException var6) {
            LOG.warn("Failed to close writer for end point: " + eldest, var6);
        } catch (InterruptedException var7) {
            LOG.warn("Interrupted when attempting to close writer for end point: " + eldest, var7);
            Thread.currentThread().interrupt();
        }

    }

    private int retireIdleWriters() {
        int count = 0;
        long now = System.currentTimeMillis();
        ArrayList retirees = new ArrayList();
        Iterator i$ = this.allWriters.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry ep = (Map.Entry)i$.next();
            if(now - ((HiveWriter)ep.getValue()).getLastUsed() > (long)this.options.getIdleTimeout().intValue()) {
                ++count;
                retirees.add(ep.getKey());
            }
        }

        i$ = retirees.iterator();

        while(i$.hasNext()) {
            HiveEndPoint var10 = (HiveEndPoint)i$.next();

            try {
                LOG.info("Closing idle Writer to Hive end point : {}", var10);
                ((HiveWriter)this.allWriters.remove(var10)).close();
            } catch (IOException var8) {
                LOG.warn("Failed to close writer for end point: {}. Error: " + var10, var8);
            } catch (InterruptedException var9) {
                LOG.warn("Interrupted when attempting to close writer for end point: " + var10, var9);
                Thread.currentThread().interrupt();
            }
        }

        return count;
    }

    public void cleanup() {
        Iterator toShutdown = this.allWriters.entrySet().iterator();

        while(toShutdown.hasNext()) {
            Map.Entry arr$ = (Map.Entry)toShutdown.next();

            try {
                HiveWriter len$ = (HiveWriter)arr$.getValue();
                LOG.info("Flushing writer to {}", len$);
                len$.flush(false);
                LOG.info("Closing writer to {}", len$);
                len$.close();
            } catch (Exception var8) {
                LOG.warn("Error while closing writer to " + arr$.getKey() + ". Exception follows.", var8);
                if(var8 instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        ExecutorService[] var9 = new ExecutorService[]{this.callTimeoutPool};
        ExecutorService[] var10 = var9;
        int var11 = var9.length;
        int i$ = 0;

        while(i$ < var11) {
            ExecutorService execService = var10[i$];
            execService.shutdown();

            while(true) {
                try {
                    if(!execService.isTerminated()) {
                        execService.awaitTermination((long)this.options.getCallTimeOut().intValue(), TimeUnit.MILLISECONDS);
                        continue;
                    }
                } catch (InterruptedException var7) {
                    LOG.warn("shutdown interrupted on " + execService, var7);
                }

                ++i$;
                break;
            }
        }

        this.callTimeoutPool = null;
    }
}

