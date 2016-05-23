package com.wicom.storm.trident;

/**
 * Created by Charbel Hobeika on 3/5/2015.
 */

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.storm.hive.bolt.mapper.DelimitedRecordHiveMapper;
import org.apache.storm.hive.common.HiveOptions;
import org.apache.storm.hive.trident.HiveStateFactory;
import org.apache.storm.hive.trident.HiveUpdater;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFilter;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;

import java.security.PrivilegedExceptionAction;

public class TopologyWithTimeStamp {

    public static class Print extends BaseFilter {
        @Override
        public boolean isKeep(TridentTuple tuple){
            System.out.println("OK: "+tuple);
            return true;
        }
    }

/*
    public static class SumFunction extends BaseFunction {

        @Override
        public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
            System.out.println(Integer.parseInt(tridentTuple.getStringByField(MyFields.CHARGINGTIME.name())));
            int sum = -1*Integer.parseInt(tridentTuple.getStringByField(MyFields.CHARGINGTIME.name()));
            tridentCollector.emit(new Values(sum));
        }
    }
*/

    public static class extractDateKey extends BaseFunction {

        @Override
        public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {

            String day_key = tridentTuple.getStringByField(MyFieldsWithTimeStamp.time_stamp.name()).substring(0,8);

            tridentCollector.emit(new Values(day_key));
        }
    }

    public static void main(String[] args) throws Exception {
        final TridentTopology topology = new TridentTopology();

        Stream tridentStream = topology.newStream("cdrs",
                new FileReaderSpout(DataSource.CDR, 100, MyFieldsWithTimeStamp.getFields(), MyFieldsWithTimeStamp.getIndices()))
                .each(MyFieldsWithTimeStamp.getFields(), new extractDateKey(), new Fields("day_key"))
                //.each(MyFieldsWithTimeStamp.getFieldsWitoutTimeStampWithDayKey() , new Print()).parallelismHint(2)
        ;


        // Hive connection configuration
        String metaStoreURI = "thrift://nex-hdp-14.nexius.com:9083";
        String dbName = "default";
        String tblName = "cdr_data";
        // Fields for possible partition
        String[] partNames = {"day_key"};
        // Fields for possible column data
        String[] colNames = {"recordtype", "callingpartynumber", "callingroaminfo", "callingcellid", "chargingtime", "imei", "callingpartyimsi", "chargingpartynumber", "paytype", "roamstate", "homezoneid", "servicetype", "subscriberid", "chargeoffundaccounts", "chargefromprepaid", "prepaidbalance", "chargefrompostpaid", "postpaidbalance", "accountid", "currencycode", "callingnetworktype", "grouppayflag", "terminationreason", "chargeduration", "chargingid", "downflux", "upflux", "sgsnaddress", "ggsnaddress", "serviceid", "callinghomecountrycode", "callinghomenetworkcode", "callingroamcountrycode", "callingroamnetworkcode", "callingvpngroupnumber", "callingvpntopgroupnumber", "chargingtype", "diametersessionid", "serialno", "subsequence"};


        // Record Writer configuration
        DelimitedRecordHiveMapper mapper = new DelimitedRecordHiveMapper()
                .withColumnFields(new Fields(colNames))
                .withPartitionFields(new Fields(partNames));
                //.withTimeAsPartitionField("YYYY/MM/DD");
        HiveOptions hiveOptions;
        hiveOptions = new HiveOptions(metaStoreURI, dbName, tblName, mapper)
                .withTxnsPerBatch(2)
                .withBatchSize(100)
                .withIdleTimeout(10);

        StateFactory factory = new HiveStateFactory().withOptions(hiveOptions);
        TridentState state = tridentStream.partitionPersist(factory,
                            MyFieldsWithTimeStamp.getFieldsWitoutTimeStampWithDayKey(),
                            new HiveUpdater(), new Fields());

        try{
            UserGroupInformation ugi= UserGroupInformation.createRemoteUser("sm_user");

            ugi.doAs(new PrivilegedExceptionAction<Void>(){
                public Void run() throws Exception{
                    Config config = new Config();
                    config.setDebug(false);
                    LocalCluster cluster = new LocalCluster();
                    cluster.submitTopology("cdr-topology", config, topology.build());
                    //cluster.shutdown();
                    return null;
                }
            });

        }catch(Exception e){
            System.out.println(e.getMessage().toString());
        }
    }
}