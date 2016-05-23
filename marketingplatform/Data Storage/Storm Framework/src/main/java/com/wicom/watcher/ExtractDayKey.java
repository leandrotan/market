package com.wicom.watcher;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Created by albert.elkhoury on 3/26/2015.
 */
public class ExtractDayKey extends BaseFunction {

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {

        String day_key = tridentTuple.getStringByField(FileNeededFields.time_stamp.name()).substring(0,8);
        tridentCollector.emit(new Values(day_key));
    }
}
