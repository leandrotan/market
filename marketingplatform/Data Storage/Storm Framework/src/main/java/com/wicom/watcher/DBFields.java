package com.wicom.watcher;

import backtype.storm.tuple.Fields;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albert.elkhoury on 3/26/2015.
 */
public enum DBFields {

    auditsid("column"),
    filename("column"),
    recordtype("column"),
    callingpartynumber("column"),
    callingroaminfo("column"),
    callingcellid("column"),
    chargingtime("column"),
    imei("column"),
    callingpartyimsi("column"),
    chargingpartynumber("column"),
    paytype("column"),
    roamstate("column"),
    homezoneid("column"),
    servicetype("column"),
    subscriberid("column"),
    chargeoffundaccounts("column"),
    chargefromprepaid("column"),
    prepaidbalance("column"),
    chargefrompostpaid("column"),
    postpaidbalance("column"),
    accountid("column"),
    currencycode("column"),
    callingnetworktype("column"),
    grouppayflag("column"),
    terminationreason("column"),
    chargeduration("column"),
    chargingid("column"),
    downflux("column"),
    upflux("column"),
    sgsnaddress("column"),
    ggsnaddress("column"),
    serviceid("column"),
    callinghomecountrycode("column"),
    callinghomenetworkcode("column"),
    callingroamcountrycode("column"),
    callingroamnetworkcode("column"),
    callingvpngroupnumber("column"),
    callingvpntopgroupnumber("column"),
    chargingtype("column"),
    diametersessionid("column"),
    serialno("column"),
    subsequence("column"),
    day_key("partition");

    public final String columnType;

    DBFields(String columnType) {
        this.columnType = columnType;
    }

    public static List<String> getColNames(){
        List<String>colnames=new ArrayList<String>();
        for(DBFields field: DBFields.values()){
            if (field.columnType == "column")
            colnames.add(field.name());
        }
        return colnames;
    }

    public static List<String> getPartNames(){
        List<String>colnames=new ArrayList<String>();
        for(DBFields field: DBFields.values()){
            if (field.columnType == "partition")
                colnames.add(field.name());
        }
        return colnames;
    }
}
