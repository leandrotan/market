package com.wicom.watcher;

import backtype.storm.tuple.Fields;

import java.util.ArrayList;
import java.util.List;

public enum FileNeededFields {
    recordtype(0),
    time_stamp  (1),
    callingpartynumber(2),
    callingroaminfo(3),
    callingcellid(4),
    chargingtime(5),
    imei(6),
    callingpartyimsi(7),
    chargingpartynumber(8),
    paytype(9),
    roamstate(10),
    homezoneid(11),
    servicetype(13),
    subscriberid(14),
    chargeoffundaccounts(15),
    chargefromprepaid(16),
    prepaidbalance(17),
    chargefrompostpaid(18),
    postpaidbalance(19),
    accountid(20),
    currencycode(21),
    callingnetworktype(22),
    grouppayflag(32),
    terminationreason(35),
    chargeduration(36),
    chargingid(56),
    downflux(57),
    upflux(58),
    sgsnaddress(59),
    ggsnaddress(60),
    serviceid(61),
    callinghomecountrycode(76),
    callinghomenetworkcode(77),
    callingroamcountrycode(79),
    callingroamnetworkcode(80),
    callingvpngroupnumber(81),
    callingvpntopgroupnumber(83),
    chargingtype(84),
    diametersessionid(85),
    serialno(87),
    subsequence(88);


    public final int index;

    FileNeededFields(int index) {
        this.index = index;
    }


    public static Fields getFieldsWitoutTimeStamp() {
        List<String> fields = new ArrayList<String>();


        for (FileNeededFields field : FileNeededFields.values()) {
            if (field.name()!=FileNeededFields.time_stamp.toString()) {
                fields.add(field.name());
            }
        }

        return new Fields(fields);
    }

    public static Fields getFieldsWitoutTimeStampWithDayKey() {
        List<String> fields = new ArrayList<String>();


        for (FileNeededFields field : FileNeededFields.values()) {
            if (field.name()!=FileNeededFields.time_stamp.toString()) {
                fields.add(field.name());
            }

        }
        fields.add("day_key");

        return new Fields(fields);
    }

    // this should be avaialble for any topology
    public static Fields getFieldsWithAuditSidFileName() {
        List<String> fields = new ArrayList<String>();
        fields.add("auditsid");
        fields.add("filename");
        fields.addAll(getFieldsWitoutTimeStampWithDayKey().toList());
        /*
        for (FileNeededFields field : FileNeededFields.values()) {
            if (field.name()!=FileNeededFields.time_stamp.toString()) {
                fields.add(field.name());
            }

        }
        */
        return new Fields(fields);
    }

    public static Fields getFieldsWitoutTimeStampWithFileName() {
        List<String> fields = new ArrayList<String>();

        fields.add("filename");
        for (FileNeededFields field : FileNeededFields.values()) {
            if (field.name()!=FileNeededFields.time_stamp.toString()) {
                fields.add(field.name());
            }

        }


        return new Fields(fields);
    }

    public static Fields getFields() {
        List<String> fields = new ArrayList<String>();


        for (FileNeededFields field : FileNeededFields.values()) {

            fields.add(field.name());
        }

        return new Fields(fields);
    }

    public static Integer[] getIndices() {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (FileNeededFields field : FileNeededFields.values()) {
            indices.add(field.index);
        }

        return indices.toArray(new Integer[indices.size()]);
    }

    public static Integer[] getIndicesWithoutTimeStamp() {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (FileNeededFields field : FileNeededFields.values()) {
            if (field.name()!=FileNeededFields.time_stamp.toString()) {
                indices.add(field.index);
            }
        }

        return indices.toArray(new Integer[indices.size()]);
    }

}
