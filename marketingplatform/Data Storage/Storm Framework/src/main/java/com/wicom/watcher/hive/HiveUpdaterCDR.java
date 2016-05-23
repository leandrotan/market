package com.wicom.watcher.hive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

import java.util.List;


public class HiveUpdaterCDR extends BaseStateUpdater<HiveStateCDR> {
    private static final Logger LOG = LoggerFactory.getLogger(HiveUpdaterCDR.class);

    public HiveUpdaterCDR() {
    }

    public void updateState(HiveStateCDR state, List<TridentTuple> tuples, TridentCollector collector) {
        LOG.warn("Calling updateState ...");
        state.updateState(tuples, collector);
    }
}
