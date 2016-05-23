package com.wicom.hive;

import backtype.storm.tuple.Fields;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albert.elkhoury on 3/5/2015.
 */
public enum HiveFields {

    RECORDTYPE(1),
    CALLINGPARTYNUMBER(2),
    CALLINGROAMINFO(3),
    CALLINGCELLID(4),
    CHARGINGTIME(5),
    IMEI(6),
    CALLINGPARTYIMSI(7),
    CHARGINGPARTYNUMBER(8),
    PAYTYPE(9),
    ROAMSTATE(10),
    HOMEZONEID(11),
    SERVICETYPE(13),
    SUBSCRIBERID(14),
    CHARGEOFFUNDACCOUNTS(15),
    CHARGEFROMPREPAID(16),
    PREPAIDBALANCE(17),
    CHARGEFROMPOSTPAID(18),
    POSTPAIDBALANCE(19),
    ACCOUNTID(20),
    CURRENCYCODE(21),
    CALLINGNETWORKTYPE(22),
    GROUPPAYFLAG(32),
    TERMINATIONREASON(35),
    CHARGEDURATION(36),
    CHARGINGID(56),
    DOWNFLUX(57),
    UPFLUX(58),
    SGSNADDRESS(59),
    GGSNADDRESS(60),
    SERVICEID(61),
    CALLINGHOMECOUNTRYCODE(76),
    CALLINGHOMENETWORKCODE(77),
    CALLINGROAMCOUNTRYCODE(79),
    CALLINGROAMNETWORKCODE(80),
    CALLINGVPNGROUPNUMBER(81),
    CALLINGVPNTOPGROUPNUMBER(83),
    CHARGINGTYPE(84),
    DIAMETERSESSIONID(85),
    SERIALNO(87),
    SUBSEQUENCE(88),
    DAY_KEY(-1);

    private final int index;

    HiveFields(int index){
        this.index=index;
    }

    public static Fields getFields(){
        List<String> fields=new ArrayList<String>();

        for(HiveFields field: HiveFields.values()){
            fields.add(field.name());
        }

        return new Fields(fields);
    }

    public static Integer[] getIndices(){
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for(HiveFields field : HiveFields.values()){
            indices.add(field.index);
        }

        return indices.toArray(new Integer[indices.size()]);
    }

}
