package com.wicom.watcher.hive;

import backtype.storm.task.IMetricsContext;
import org.apache.storm.hive.common.HiveOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

import java.util.Map;

/**
 * Created by albert.elkhoury on 3/23/2015.
 */
public class HiveStateFactoryCDR implements StateFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HiveStateFactoryCDR.class);
    private HiveOptions options;

    public HiveStateFactoryCDR() {
    }

    public HiveStateFactoryCDR withOptions(HiveOptions options) {
        this.options = options;
        return this;
    }
    @Override
    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
        LOG.warn("makeState(partitonIndex={}, numpartitions={}", Integer.valueOf(partitionIndex), Integer.valueOf(numPartitions));
        HiveStateCDR state = new HiveStateCDR(this.options);
        state.prepare(conf, metrics, partitionIndex, numPartitions);
        return state;
    }
}
