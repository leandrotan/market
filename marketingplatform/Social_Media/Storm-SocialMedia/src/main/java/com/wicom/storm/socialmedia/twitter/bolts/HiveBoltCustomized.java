//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wicom.storm.socialmedia.twitter.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

/*
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.storm.hive.common.HiveOptions;
import org.apache.storm.hive.common.HiveUtils;
import org.apache.storm.hive.common.HiveWriter;
import org.apache.storm.hive.common.HiveUtils.AuthenticationFailed;
import org.apache.storm.hive.common.HiveWriter.CommitFailure;
import org.apache.storm.hive.common.HiveWriter.ConnectFailure;
import org.apache.storm.hive.common.HiveWriter.TxnBatchFailure;
import org.apache.storm.hive.common.HiveWriter.TxnFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
public class HiveBoltCustomized extends BaseRichBolt {
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

    }

    @Override
    public void execute(Tuple input) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
    /*private static final Logger LOG = LoggerFactory.getLogger(HiveBoltCustomized.class);
    private OutputCollector collector;
    private HiveOptions options;
    private Integer currentBatchSize;
    private ExecutorService callTimeoutPool;
    private transient Timer heartBeatTimer;
    private Boolean kerberosEnabled = Boolean.valueOf(false);
    private AtomicBoolean timeToSendHeartBeat = new AtomicBoolean(false);
    private UserGroupInformation ugi = null;
    HashMap<HiveEndPoint, HiveWriter> allWriters;

    public HiveBoltCustomized(HiveOptions options) {
        this.options = options;
        this.currentBatchSize = Integer.valueOf(0);
        System.out.println("Test Customized Bolt");
    }

    public void prepare(Map conf, TopologyContext topologyContext, OutputCollector collector) {
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
                } catch (AuthenticationFailed var5) {
                    System.out.println("Hive Kerberos authentication failed " + var5.getMessage());
                    throw new IllegalArgumentException(var5);
                }
            }

            this.collector = collector;
            this.allWriters = new HashMap();
            String e = "hive-bolt-%d";
            this.callTimeoutPool = Executors.newFixedThreadPool(1, (new ThreadFactoryBuilder()).setNameFormat(e).build());
            this.heartBeatTimer = new Timer();
            this.setupHeartBeatTimer();
        } catch (Exception var6) {
            System.out.println("unable to make connection to hive "+ var6.getMessage());
        }

    }

    public void execute(Tuple tuple) {
        try {
            List e = this.options.getMapper().mapPartitions(tuple);
            HiveEndPoint endPoint = HiveUtils.makeEndPoint(e, this.options);
            HiveWriter writer = this.getOrCreateWriter(endPoint);
            if(this.timeToSendHeartBeat.compareAndSet(true, false)) {
                this.enableHeartBeatOnAllWriters();
            }

            writer.write(this.options.getMapper().mapRecord(tuple));
            Integer var5 = this.currentBatchSize;
            Integer var6 = this.currentBatchSize = Integer.valueOf(this.currentBatchSize.intValue() + 1);
            if(this.currentBatchSize.intValue() >= this.options.getBatchSize().intValue()) {
                this.flushAllWriters();
                this.currentBatchSize = Integer.valueOf(0);
            }

            this.collector.ack(tuple);
        } catch (Exception var7) {
            System.out.println(var7.getMessage());
            this.collector.fail(tuple);
            this.flushAndCloseWriters();
            System.out.println("hive streaming failed. "+ var7);
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    public void cleanup() {
        Iterator toShutdown = this.allWriters.entrySet().iterator();

        while(toShutdown.hasNext()) {
            Entry arr$ = (Entry)toShutdown.next();

            try {
                HiveWriter len$ = (HiveWriter)arr$.getValue();
                System.out.println("Flushing writer to {}"+ len$);
                len$.flush(false);
                System.out.println("Closing writer to {}"+ len$);
                len$.close();
            } catch (Exception var8) {
                System.out.println("Error while closing writer to " + arr$.getKey() + ". Exception follows."+ var8);
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
                    System.out.println("shutdown interrupted on " + execService+ var7);
                }

                ++i$;
                break;
            }
        }

        this.callTimeoutPool = null;
        super.cleanup();
        System.out.println("Hive Bolt stopped");
    }

    private void setupHeartBeatTimer() {
        if(this.options.getHeartBeatInterval().intValue() > 0) {
            this.heartBeatTimer.schedule(new TimerTask() {
                public void run() {
                    HiveBoltCustomized.this.timeToSendHeartBeat.set(true);
                    HiveBoltCustomized.this.setupHeartBeatTimer();
                }
            }, (long)(this.options.getHeartBeatInterval().intValue() * 1000));
        }

    }

    private void flushAllWriters() throws CommitFailure, TxnBatchFailure, TxnFailure, InterruptedException {
        Iterator i$ = this.allWriters.values().iterator();

        while(i$.hasNext()) {
            HiveWriter writer = (HiveWriter)i$.next();
            writer.flush(true);
        }

    }

    private void closeAllWriters() {
        try {
            Iterator e = this.allWriters.entrySet().iterator();

            while(e.hasNext()) {
                Entry entry = (Entry)e.next();
                ((HiveWriter)entry.getValue()).close();
            }

            this.allWriters.clear();
        } catch (Exception var3) {
            System.out.println("unable to close writers. "+ var3);
        }

    }

    private void flushAndCloseWriters() {
        try {
            this.flushAllWriters();
        } catch (Exception var5) {
            System.out.println("unable to flush hive writers. "+ var5);
        } finally {
            this.closeAllWriters();
        }

    }

    private void enableHeartBeatOnAllWriters() {
        Iterator i$ = this.allWriters.values().iterator();

        while(i$.hasNext()) {
            HiveWriter writer = (HiveWriter)i$.next();
            writer.setHeartBeatNeeded();
        }

    }

    private HiveWriter getOrCreateWriter(HiveEndPoint endPoint) throws ConnectFailure, InterruptedException {
        try {
            HiveWriter e = (HiveWriter)this.allWriters.get(endPoint);
            if(e == null) {
                System.out.println("Creating Writer to Hive end point : " + endPoint);
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
        } catch (ConnectFailure var4) {
            System.out.println("Failed to create HiveWriter for endpoint: " + endPoint+ var4);
            throw var4;
        }
    }

    private void retireEldestWriter() {
        long oldestTimeStamp = System.currentTimeMillis();
        HiveEndPoint eldest = null;
        Iterator e = this.allWriters.entrySet().iterator();

        while(e.hasNext()) {
            Entry entry = (Entry)e.next();
            if(((HiveWriter)entry.getValue()).getLastUsed() < oldestTimeStamp) {
                eldest = (HiveEndPoint)entry.getKey();
                oldestTimeStamp = ((HiveWriter)entry.getValue()).getLastUsed();
            }
        }

        try {
            System.out.println("Closing least used Writer to Hive end point : " + eldest);
            ((HiveWriter)this.allWriters.remove(eldest)).close();
        } catch (IOException var6) {
            System.out.println("Failed to close writer for end point: " + eldest+var6);
        } catch (InterruptedException var7) {
            System.out.println("Interrupted when attempting to close writer for end point: " + eldest+ var7);
            Thread.currentThread().interrupt();
        }

    }

    private int retireIdleWriters() {
        int count = 0;
        long now = System.currentTimeMillis();
        ArrayList retirees = new ArrayList();
        Iterator i$ = this.allWriters.entrySet().iterator();

        while(i$.hasNext()) {
            Entry ep = (Entry)i$.next();
            if(now - ((HiveWriter)ep.getValue()).getLastUsed() > (long)this.options.getIdleTimeout().intValue()) {
                ++count;
                retirees.add(ep.getKey());
            }
        }

        i$ = retirees.iterator();

        while(i$.hasNext()) {
            HiveEndPoint var10 = (HiveEndPoint)i$.next();

            try {
                System.out.println("Closing idle Writer to Hive end point : {}"+ var10);
                ((HiveWriter)this.allWriters.remove(var10)).close();
            } catch (IOException var8) {
                System.out.println("Failed to close writer for end point: {}. Error: " + var10+ var8);
            } catch (InterruptedException var9) {
                System.out.println("Interrupted when attempting to close writer for end point: " + var10+ var9);
                Thread.currentThread().interrupt();
            }
        }

        return count;
    } */
}
