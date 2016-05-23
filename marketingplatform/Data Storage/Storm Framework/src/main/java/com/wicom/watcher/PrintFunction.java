package com.wicom.watcher;

/**
 * Created by Charbel Hobeika on 3/19/2015.
 */
import backtype.storm.tuple.Fields;
import com.wicom.watcher.audit.HbaseAudit;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class PrintFunction implements Function {
    @Override
    public void execute(TridentTuple tuple, TridentCollector tridentCollector) {
                /*
        String lastFieldValue=tuple.getValue(tuple.getFields().toList().size()-1).toString();
        System.out.println(lastFieldValue);
        if(lastFieldValue!=null) {
            if (lastFieldValue.equalsIgnoreCase("File End")) {
                System.out.println("Reaching File End : " + tuple.getValue(1).toString());

                //LOG.warn("Next step is to update dim_audit for file : " + tuple.getValue(1).toString() + "with audit_sid = " + tuple.getValue(0));
            }
        }


        String lastFieldValue=tuple.getValue(tuple.getFields().toList().size()-1).toString();
        if(lastFieldValue!=null) {
            if (lastFieldValue.equalsIgnoreCase("File End")) {
                //System.out.println(tuple);
                System.out.println("Reaching File End : " + tuple.getValue(1).toString());
                //LOG.warn("Next step is to update dim_audit for file : " + tuple.getValue(1).toString() + "with audit_sid = " + tuple.getValue(0));
            }
        }
        */
        System.out.println(tuple);
        tridentCollector.emit(tuple);

    }

    @Override
    public void prepare(Map map, TridentOperationContext tridentOperationContext) {

    }

    @Override
    public void cleanup() {

    }
}