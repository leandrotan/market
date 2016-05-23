package com.wicom.hive;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.vertica.jdbc.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseAggregator;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by albert.elkhoury on 3/5/2015.
 */
public class HiveReaderTopology {

    public static class PrintFunction extends BaseFunction {
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            System.out.println(tuple);
        }
    }

    public static class VerticaCopyWriter extends BaseFunction {
        final Logger logger = LoggerFactory.getLogger(HiveReaderTopology.class);
        Connection conn=null;
        Statement stmt=null;
        DataSource verticaDS=null;

        @Override
        public void prepare(Map conf, TridentOperationContext context){
            verticaDS=new DataSource();
            verticaDS.setHost((String) conf.get(HiveReaderTopologyConstants.VERTICA_SERVER));
            verticaDS.setPort(Short.parseShort(((String) conf.get(HiveReaderTopologyConstants.VERTICA_PORT))));
            verticaDS.setDatabase((String) conf.get(HiveReaderTopologyConstants.VERTICA_DB));
            verticaDS.setUserID((String) conf.get(HiveReaderTopologyConstants.VERTICA_USER));
            verticaDS.setPassword((String) conf.get(HiveReaderTopologyConstants.VERTICA_PASS));
            verticaDS.setAutoCommitOnByDefault(false);

            try {
                conn=verticaDS.getConnection();
                stmt = conn.createStatement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            try {
                logger.warn("Initializing Stream...");
                boolean result=stmt.execute("COPY storm_cdr_data_agg FROM LOCAL '"+tuple.getStringByField("FILENAME")+"'"); // DIRECT ENFORCELENGTH
                if(result) {
                    logger.warn("COPY Statement executed successfully...");
                }
                else{
                    logger.error("Error executing copy statement..");
                }
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void cleanup(){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class PrepareToCopy extends BaseAggregator<PrepareToCopy.State>{
        private static final long serialVersionUID =1L;
        final Logger logger = LoggerFactory.getLogger(HiveReaderTopology.class);

        static class State{
            String filename;
            File tempFile;
            FileOutputStream fos;
            int rowCount;

            public State() throws IOException {
                this.filename="cdr_data_aggregated_"+(new Date()).getTime();;
                tempFile=File.createTempFile(filename, ".tmp");
                fos=new FileOutputStream(tempFile);
                rowCount=0;
            }

        }

        public State init(Object batchId,TridentCollector tridentCollector){
            State st = null;
            try {
                st= new State();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return st;
        }

        public void aggregate(State state,TridentTuple tridentTuple,TridentCollector tridentCollector){

            String row=new String();
            int i=0;
            for (String field :tridentTuple.getFields())
            {
                //if(!tuple.getStringByField(field).isEmpty()){
                row=i>0?row+"|"+String.valueOf(tridentTuple.getValue(i)):row+String.valueOf(tridentTuple.getValue(i));
                //}
                i++;
            }
            logger.warn("Appending row #"+state.rowCount+" - content:"+row);
            try {
                state.fos.write(row.getBytes());
                state.fos.write(System.getProperty("line.separator").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            state.rowCount++;
        }

        public void complete(State state,TridentCollector tridentCollector){
            tridentCollector.emit(new Values(state.tempFile.getAbsolutePath(),state.rowCount));
        }
    }

    public static class SumAsAggregator extends BaseAggregator<SumAsAggregator.State>{
        private static final long serialVersionUID =1L;

        static class State{
            String msisdn=new String();
            int day_key=0;
            long totalCharged=0;
            long totalUp=0;
            long totalDown=0;
        }

        public State init(Object batchId,TridentCollector tridentCollector){
            return new State();
        }

        public void aggregate(State state,TridentTuple tridentTuple,TridentCollector tridentCollector){

            if(!tridentTuple.getStringByField(HiveFields.CALLINGPARTYNUMBER.name()).isEmpty()){
                state.msisdn=tridentTuple.getStringByField(HiveFields.CALLINGPARTYNUMBER.name());
            }
            if(!tridentTuple.getStringByField(HiveFields.DAY_KEY.name()).isEmpty()){
                state.day_key= Integer.parseInt(tridentTuple.getStringByField(HiveFields.DAY_KEY.name()));
            }
            if(!tridentTuple.getStringByField(HiveFields.CHARGEDURATION.name()).isEmpty()){
                state.totalCharged = state.totalCharged + Long.parseLong(tridentTuple.getStringByField(HiveFields.CHARGEDURATION.name()));
            }
            if(!tridentTuple.getStringByField(HiveFields.UPFLUX.name()).isEmpty()) {
                state.totalUp = state.totalUp + Long.parseLong(tridentTuple.getStringByField(HiveFields.UPFLUX.name()));
            }
            if(!tridentTuple.getStringByField(HiveFields.DOWNFLUX.name()).isEmpty()) {
                state.totalDown = state.totalDown + Long.parseLong(tridentTuple.getStringByField(HiveFields.DOWNFLUX.name()));
            }
        }

        public void complete(State state,TridentCollector tridentCollector){
            tridentCollector.emit(new Values(state.day_key,state.msisdn,state.totalCharged,state.totalUp,state.totalDown,state.totalUp+state.totalDown));
        }
    }



    public static void main(String[] args) throws Exception{

        Map<String,String> props = new HashMap<String, String>();
        Properties prop= new Properties();
        if(args.length>=1){
            FileReader propReader=new FileReader(args[0]);
            prop.load(propReader);
            props.put(HiveReaderTopologyConstants.HIVE_DRIVER,prop.getProperty(HiveReaderTopologyConstants.HIVE_DRIVER));
            props.put(HiveReaderTopologyConstants.VERTICA_DRIVER,prop.getProperty(HiveReaderTopologyConstants.VERTICA_DRIVER));
            props.put(HiveReaderTopologyConstants.HIVE_SERVER,prop.getProperty(HiveReaderTopologyConstants.HIVE_SERVER));
            props.put(HiveReaderTopologyConstants.VERTICA_SERVER,prop.getProperty(HiveReaderTopologyConstants.VERTICA_SERVER));//"10.9.10.73"
            props.put(HiveReaderTopologyConstants.HIVE_PORT,prop.getProperty(HiveReaderTopologyConstants.HIVE_PORT));
            props.put(HiveReaderTopologyConstants.VERTICA_PORT,prop.getProperty(HiveReaderTopologyConstants.VERTICA_PORT));
            props.put(HiveReaderTopologyConstants.HIVE_DB,prop.getProperty(HiveReaderTopologyConstants.HIVE_DB));
            props.put(HiveReaderTopologyConstants.VERTICA_DB,prop.getProperty(HiveReaderTopologyConstants.VERTICA_DB));//"VMartDB"
            props.put(HiveReaderTopologyConstants.HIVE_USER,prop.getProperty(HiveReaderTopologyConstants.HIVE_USER));
            props.put(HiveReaderTopologyConstants.HIVE_PASS,prop.getProperty(HiveReaderTopologyConstants.HIVE_PASS));
            props.put(HiveReaderTopologyConstants.VERTICA_USER,prop.getProperty(HiveReaderTopologyConstants.VERTICA_USER));//"dbadmin"
            props.put(HiveReaderTopologyConstants.VERTICA_PASS,prop.getProperty(HiveReaderTopologyConstants.VERTICA_PASS));//"password"
            props.put(HiveReaderTopologyConstants.HIVE_READER_QUERY, prop.getProperty(HiveReaderTopologyConstants.HIVE_READER_QUERY));
        } else{
            props.put(HiveReaderTopologyConstants.HIVE_DRIVER,"org.apache.hive.jdbc.HiveDriver");
            props.put(HiveReaderTopologyConstants.VERTICA_DRIVER,"com.vertica.jdbc.Driver");
            props.put(HiveReaderTopologyConstants.HIVE_SERVER,"nex-hdp-14.nexius.com");
            props.put(HiveReaderTopologyConstants.VERTICA_SERVER,"10.104.5.28");//"10.9.10.73"
            props.put(HiveReaderTopologyConstants.HIVE_PORT,"10000");
            props.put(HiveReaderTopologyConstants.VERTICA_PORT,"5433");
            props.put(HiveReaderTopologyConstants.HIVE_DB,"default");
            props.put(HiveReaderTopologyConstants.VERTICA_DB,"verticadst");//"VMartDB"
            props.put(HiveReaderTopologyConstants.HIVE_USER,"sm_user");
            props.put(HiveReaderTopologyConstants.HIVE_PASS,"hdp-08/home");
            props.put(HiveReaderTopologyConstants.VERTICA_USER,"alfxpldev");//"dbadmin"
            props.put(HiveReaderTopologyConstants.VERTICA_PASS,"xpl123");//"password"
            props.put(HiveReaderTopologyConstants.HIVE_READER_QUERY,"select * from default.cdr_data");

        }


        TridentTopology topology=new TridentTopology();
        topology.newStream("hivereader", new HiveReaderSpout(HiveFields.getFields()))
                .groupBy(new Fields(HiveFields.CALLINGPARTYNUMBER.name(),HiveFields.DAY_KEY.name()))
                .partitionAggregate(new Fields(HiveFields.DAY_KEY.name(), HiveFields.CALLINGPARTYNUMBER.name(), HiveFields.CHARGEDURATION.name(),
                                HiveFields.UPFLUX.name(), HiveFields.DOWNFLUX.name()),
                        new SumAsAggregator(),
                        new Fields("DAY_KEY1", "MSISDN", "CHARGED_DURATION", "TOTAL_UPFLUX", "TOTAL_DOWNFLUX", "TOTAL_FLUX"))
                .toStream()
                .groupBy(new Fields("DAY_KEY1"))
                .partitionAggregate(new Fields("DAY_KEY1", "MSISDN", "CHARGED_DURATION", "TOTAL_UPFLUX", "TOTAL_DOWNFLUX", "TOTAL_FLUX"),
                        new PrepareToCopy(),
                        new Fields("FILENAME", "ROW_COUNT"))
                .each(new Fields("FILENAME", "ROW_COUNT"), new VerticaCopyWriter(), new Fields());


        Config conf = new Config();
        conf.setMaxSpoutPending(3);
        conf.putAll(props);
        conf.setDebug(false);
        if(args.length>1){
            conf.setNumWorkers(3);
            StormSubmitter.submitTopology(args[1], conf, topology.build());
        }
        else{
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("HiveRead",conf,topology.build());
            //Thread.sleep(15*1000);
            //cluster.shutdown();
        }

    }
}
