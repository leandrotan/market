package com.wicom.storm.trident;

/**
 * Created by Charbel Hobeika on 3/5/2015.
 */
import storm.trident.tuple.TridentTuple;
import storm.trident.operation.BaseFilter;

public class CallingRoaminFoFilter extends BaseFilter {

    @Override
    public boolean isKeep(TridentTuple tridentTuple) {
        int CALLINGROAMINFO = Integer.parseInt(tridentTuple.getStringByField(MyFieldsWithTimeStamp.callingroaminfo.name()));
        //System.out.println("Second Field: "+CALLINGROAMINFO);

        return CALLINGROAMINFO != 42604;
    }
}
