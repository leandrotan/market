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

public class TopologyWithTimeStamp_ConsoleOutput {

    public static class Print extends BaseFilter {
        @Override
        public boolean isKeep(TridentTuple tuple){
            System.out.println("OK: "+tuple);
            return true;
        }
    }

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
                .each(MyFieldsWithTimeStamp.getFieldsWitoutTimeStampWithDayKey() , new Print()).parallelismHint(2)
        ;

        Config config = new Config();
        config.setDebug(false);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("cdr-topology", config, topology.build());

        System.out.println("Done Topology Ended");

    }
}