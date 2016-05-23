package com.wicom.watcher.DB;

import com.ibatis.common.jdbc.ScriptRunner;
import com.vertica.jdbc.DataSource;
import com.wicom.watcher.GenericConstants;
import com.wicom.watcher.audit.HbaseAudit;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by Charbel Hobeika on 4/20/2015.
 */
public class VerticaCopyWriter extends BaseFunction {
    final Logger LOGGER = LoggerFactory.getLogger(VerticaCopyWriter.class);
    Connection conn=null;
    Statement stmt=null;
    DataSource verticaDS=null;
    String csvFileName;
    String copyCommandFilePath;
    String copyCommandRejectedDataFile;
    String factTable;
    String populateNetworkAndDictionary = null;
    private String auditDbAuditTable;
    HbaseAudit HbA;

    @Override
    public void prepare(Map conf, TridentOperationContext context){
        this.copyCommandFilePath = (String) conf.get(GenericConstants.TRANSFORMER_FILE_INCOMING_DIRECTORY);
        this.copyCommandRejectedDataFile = (String) conf.get(GenericConstants.TRANSFORMER_FILE_ERROR_DIRECTORY);
        this.populateNetworkAndDictionary = (String) conf.get(GenericConstants.TRANSFORMER_FILE_NETWORKANDDICTIONARY_POPULATE);

        this.verticaDS=new DataSource();
        this.verticaDS.setHost((String) conf.get(GenericConstants.VERTICA_SERVER));
        this.verticaDS.setPort(Short.parseShort(((String) conf.get(GenericConstants.VERTICA_PORT))));
        this.verticaDS.setDatabase((String) conf.get(GenericConstants.VERTICA_DB));
        this.verticaDS.setUserID((String) conf.get(GenericConstants.VERTICA_USER));
        this.verticaDS.setPassword((String) conf.get(GenericConstants.VERTICA_PASS));
        this.verticaDS.setAutoCommitOnByDefault(false);



        this.auditDbAuditTable= (String) conf.get(GenericConstants.AUDIT_DB_AUDITTABLE);
        this.HbA = new HbaseAudit(this.auditDbAuditTable);

        this.factTable = (String) conf.get(GenericConstants.VERTICA_DB_FACT_TABLE);

        try {
            this.conn=verticaDS.getConnection();
            this.stmt = conn.createStatement();
            LOGGER.warn("Connection to: "+stmt.getConnection().getMetaData().getDatabaseProductName()+" aquired!");
        } catch (Exception e) {
            LOGGER.warn("Unable to aquire connection.");
            e.printStackTrace();
        }
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        //csvFileName = "C20150426.1800+0300-20150426.1900+0300_NBSC7_1000.csv";
        csvFileName = tuple.getStringByField("csvFileName");
        Integer hourSid = Integer.valueOf(csvFileName.substring(10, 12));
        String auditSid="";

        String fullFilePath = null;
        if (copyCommandFilePath.endsWith("/") || copyCommandFilePath.endsWith("\\"))
            fullFilePath = copyCommandFilePath + csvFileName;
        else fullFilePath = copyCommandFilePath+"/"+csvFileName;

        String rejectedFullFilePath = null;
        if (copyCommandRejectedDataFile.endsWith("/") || copyCommandRejectedDataFile.endsWith("\\"))
            rejectedFullFilePath = copyCommandRejectedDataFile + csvFileName +".rejected";
        else rejectedFullFilePath = copyCommandRejectedDataFile+"/"+csvFileName +".rejected";

        try {
            auditSid=HbA.logAudit("csv Loading",fullFilePath);
           // auditSid="20140504";
            //System.out.println(countLines(fullFilePath));

            LOGGER.warn("Initializing Stream...");
            LOGGER.warn("Vertica Default column Delimiteris: |");
            // Adding DIRECT To the COPY Command to allow Loading Directly to ROS
            String command = "COPY "+ factTable + " (network_id,date_sid,date_id,hour_sid,second_sid,audit_sid AS "+ auditSid+ ",numeric1,numeric2,numeric3,numeric4,numeric5,numeric6,numeric7,numeric8,numeric9,numeric10,numeric11,numeric12,numeric13,numeric14,numeric15,numeric16,numeric17,numeric18,numeric19,numeric20,numeric21,numeric22,numeric23,numeric24,numeric25,numeric26,numeric27,numeric28,numeric29,numeric30,numeric31,numeric32,numeric33,numeric34,numeric35,numeric36,numeric37,numeric38,numeric39,numeric40,numeric41,numeric42,numeric43,numeric44,numeric45,numeric46,numeric47,numeric48,numeric49,numeric50,numeric51,numeric52,numeric53,numeric54,numeric55,numeric56,numeric57,numeric58,numeric59,numeric60,numeric61,numeric62,numeric63,numeric64,numeric65,numeric66,numeric67,numeric68,numeric69,numeric70,numeric71,numeric72,numeric73,numeric74,numeric75,numeric76,numeric77,numeric78,numeric79,numeric80,numeric81,numeric82,numeric83,numeric84,numeric85,numeric86,numeric87,numeric88,numeric89,numeric90,numeric91,numeric92,numeric93,numeric94,numeric95,numeric96,numeric97,numeric98,numeric99,numeric100,numeric101,numeric102,numeric103,numeric104,numeric105,numeric106,numeric107,numeric108,numeric109,numeric110,numeric111,numeric112,numeric113,numeric114,numeric115,numeric116,numeric117,numeric118,numeric119,numeric120,numeric121,numeric122,numeric123,numeric124,numeric125,numeric126,numeric127,numeric128,numeric129,numeric130,numeric131,numeric132,numeric133,numeric134,numeric135,numeric136,numeric137,numeric138,numeric139,numeric140,numeric141,numeric142,numeric143,numeric144,numeric145,numeric146,numeric147,numeric148,numeric149,numeric150,numeric151,numeric152,numeric153,numeric154,numeric155,numeric156,numeric157,numeric158,numeric159,numeric160,numeric161,numeric162,numeric163,numeric164,numeric165,numeric166,numeric167,numeric168,numeric169,numeric170,numeric171,numeric172,numeric173,numeric174,numeric175,numeric176,numeric177,numeric178,numeric179,numeric180,numeric181,numeric182,numeric183,numeric184,numeric185,numeric186,numeric187,numeric188,numeric189,numeric190,numeric191,numeric192,numeric193,numeric194,numeric195,numeric196,numeric197,numeric198,numeric199,numeric200,numeric201,numeric202,numeric203,numeric204,numeric205,numeric206,numeric207,numeric208,numeric209,numeric210,numeric211,numeric212,numeric213,numeric214,numeric215,numeric216,numeric217,numeric218,numeric219,numeric220,numeric221,numeric222,numeric223,numeric224,numeric225,numeric226,numeric227,numeric228,numeric229,numeric230,numeric231,numeric232,numeric233,numeric234,numeric235,numeric236,numeric237,numeric238,numeric239,numeric240,numeric241,numeric242,numeric243,numeric244,numeric245,numeric246,numeric247,numeric248,numeric249,numeric250,numeric251,numeric252,numeric253,numeric254,numeric255,numeric256,numeric257,numeric258,numeric259,numeric260,numeric261,numeric262,numeric263,numeric264,numeric265,numeric266,numeric267,numeric268,numeric269,numeric270,numeric271,numeric272,numeric273,numeric274,numeric275,numeric276,numeric277,numeric278,numeric279,numeric280,numeric281,numeric282,numeric283,numeric284,numeric285,numeric286,numeric287,numeric288,numeric289,numeric290,numeric291,numeric292,numeric293,numeric294,numeric295,numeric296,numeric297,numeric298,numeric299,numeric300,numeric301,numeric302,numeric303,numeric304,numeric305,numeric306,numeric307,numeric308,numeric309,numeric310,numeric311,numeric312,numeric313,numeric314,numeric315,numeric316,numeric317,numeric318,numeric319,numeric320,numeric321,numeric322,numeric323,numeric324,numeric325,numeric326,numeric327,numeric328,numeric329,numeric330,numeric331,numeric332,numeric333,numeric334,numeric335,numeric336,numeric337,numeric338,numeric339,numeric340,numeric341,numeric342,numeric343,numeric344,numeric345,numeric346,numeric347,numeric348,numeric349,numeric350,numeric351,numeric352,numeric353,numeric354,numeric355,numeric356,numeric357,numeric358,numeric359,numeric360,numeric361,numeric362,numeric363,numeric364,numeric365,numeric366,numeric367,numeric368,numeric369,numeric370,numeric371,numeric372,numeric373,numeric374,numeric375,numeric376,numeric377,numeric378,numeric379,numeric380,numeric381,numeric382,numeric383,numeric384,numeric385,numeric386,numeric387,numeric388,numeric389,numeric390,numeric391,numeric392,numeric393,numeric394,numeric395,numeric396,numeric397,numeric398,numeric399,numeric400,numeric401,numeric402,numeric403,numeric404,numeric405,numeric406,numeric407,numeric408,numeric409,numeric410,numeric411,numeric412,numeric413,numeric414,numeric415,numeric416,numeric417,numeric418,numeric419,numeric420,numeric421,numeric422,numeric423,numeric424,numeric425,numeric426,numeric427,numeric428,numeric429,numeric430,numeric431,numeric432,numeric433,numeric434,numeric435,numeric436,numeric437,numeric438,numeric439,numeric440,numeric441,numeric442,numeric443,numeric444,numeric445,numeric446,numeric447,numeric448,numeric449,numeric450,numeric451,numeric452,numeric453,numeric454,numeric455,numeric456,numeric457,numeric458,numeric459,numeric460,numeric461,numeric462,numeric463,numeric464,numeric465,numeric466,numeric467,numeric468,numeric469,numeric470,numeric471,numeric472,numeric473,numeric474,numeric475,numeric476,numeric477,numeric478,numeric479,numeric480,numeric481,numeric482,numeric483,numeric484,numeric485,numeric486,numeric487,numeric488,numeric489,numeric490,numeric491,numeric492,numeric493,numeric494,numeric495,numeric496,numeric497,numeric498,numeric499,numeric500,numeric501,numeric502,numeric503,numeric504,numeric505,numeric506,numeric507,numeric508,numeric509,numeric510,numeric511,numeric512,numeric513,numeric514,numeric515,numeric516,numeric517,numeric518,numeric519,numeric520,numeric521,numeric522,numeric523,numeric524,numeric525,numeric526,numeric527,numeric528,numeric529,numeric530,numeric531,numeric532,numeric533,numeric534,numeric535,numeric536,numeric537,numeric538,numeric539,numeric540,numeric541,numeric542,numeric543,numeric544,numeric545,numeric546,numeric547,numeric548,numeric549,numeric550,numeric551,numeric552,numeric553,numeric554,numeric555,numeric556,numeric557,numeric558,numeric559,numeric560,numeric561,numeric562,numeric563,numeric564,numeric565,numeric566,numeric567,numeric568,numeric569,numeric570,numeric571,numeric572,numeric573,numeric574,numeric575,numeric576,numeric577,numeric578,numeric579,numeric580,numeric581,numeric582,numeric583,numeric584,numeric585,numeric586,numeric587,numeric588,numeric589,numeric590,numeric591,numeric592,numeric593,numeric594,numeric595,numeric596,numeric597,numeric598,numeric599,numeric600,numeric601,numeric602,numeric603,numeric604,numeric605,numeric606,numeric607,numeric608,numeric609,numeric610,numeric611,numeric612,numeric613,numeric614,numeric615,numeric616,numeric617,numeric618,numeric619,numeric620,numeric621,numeric622,numeric623,numeric624,numeric625,numeric626,numeric627,numeric628,numeric629,numeric630,numeric631,numeric632,numeric633,numeric634,numeric635,numeric636,numeric637,numeric638,numeric639,numeric640,numeric641,numeric642,numeric643,numeric644,numeric645,numeric646,numeric647,numeric648,numeric649,numeric650,numeric651,numeric652,numeric653,numeric654,numeric655,numeric656,numeric657,numeric658,numeric659,numeric660,numeric661,numeric662,numeric663,numeric664,numeric665,numeric666,numeric667,numeric668,numeric669,numeric670,numeric671,numeric672,numeric673,numeric674,numeric675,numeric676,numeric677,numeric678,numeric679,numeric680,numeric681,numeric682,numeric683,numeric684,numeric685,numeric686,numeric687,numeric688,numeric689,numeric690,numeric691,numeric692,numeric693,numeric694,numeric695,numeric696,numeric697,numeric698,numeric699,numeric700,numeric701,numeric702,numeric703,numeric704,numeric705,numeric706,numeric707,numeric708,numeric709,numeric710,numeric711,numeric712,numeric713,numeric714,numeric715,numeric716,numeric717,numeric718,numeric719,numeric720,numeric721,numeric722,numeric723,numeric724,numeric725,numeric726,numeric727,numeric728,numeric729,numeric730,numeric731,numeric732,numeric733,numeric734,numeric735,numeric736,numeric737,numeric738,numeric739,numeric740,numeric741,numeric742,numeric743,numeric744,numeric745,numeric746,numeric747,numeric748,numeric749,numeric750,numeric751,numeric752,numeric753,numeric754,numeric755,numeric756,numeric757,numeric758,numeric759,numeric760,numeric761,numeric762,numeric763,numeric764,numeric765,numeric766,numeric767,numeric768,numeric769,numeric770,numeric771,numeric772,numeric773,numeric774,numeric775,numeric776,numeric777,numeric778,numeric779,numeric780,numeric781,numeric782,numeric783,numeric784,numeric785,numeric786,numeric787,numeric788,numeric789,numeric790,numeric791,numeric792,numeric793,numeric794,numeric795,numeric796,numeric797,numeric798,numeric799,numeric800,numeric801,numeric802,numeric803,numeric804,numeric805,numeric806,numeric807,numeric808,numeric809,numeric810,numeric811,numeric812,numeric813,numeric814,numeric815,numeric816,numeric817,numeric818,numeric819,numeric820,numeric821,numeric822,numeric823,numeric824,numeric825,numeric826,numeric827,numeric828,numeric829,numeric830,numeric831,numeric832,numeric833,numeric834,numeric835,numeric836,numeric837,numeric838,numeric839,numeric840,numeric841,numeric842,numeric843,numeric844,numeric845,numeric846,numeric847,numeric848,numeric849,numeric850,numeric851,numeric852,numeric853,numeric854,numeric855,numeric856,numeric857,numeric858,numeric859,numeric860,numeric861,numeric862,numeric863,numeric864,numeric865,numeric866,numeric867,numeric868,numeric869,numeric870,numeric871,numeric872,numeric873,numeric874,numeric875,numeric876,numeric877,numeric878,numeric879,numeric880,numeric881,numeric882,numeric883,numeric884,numeric885,numeric886,numeric887,numeric888,numeric889,numeric890,numeric891,numeric892,numeric893,numeric894,numeric895,numeric896,numeric897,numeric898,numeric899,numeric900,numeric901,numeric902,numeric903,numeric904,numeric905,numeric906,numeric907,numeric908,numeric909,numeric910,numeric911,numeric912,numeric913,numeric914,numeric915,numeric916,numeric917,numeric918,numeric919,numeric920,numeric921,numeric922,numeric923,numeric924,numeric925,numeric926,numeric927,numeric928,numeric929,numeric930,numeric931,numeric932,numeric933,numeric934,numeric935,numeric936,numeric937,numeric938,numeric939,numeric940,numeric941,numeric942,numeric943,numeric944,numeric945,numeric946,numeric947,numeric948,numeric949,numeric950,numeric951,numeric952,numeric953,numeric954,numeric955,numeric956,numeric957,numeric958,numeric959,numeric960,numeric961,numeric962,numeric963,numeric964,numeric965,numeric966,numeric967,numeric968,numeric969,numeric970,numeric971,numeric972,numeric973,numeric974,numeric975,numeric976,numeric977,numeric978,numeric979,numeric980,numeric981,numeric982,numeric983,numeric984,numeric985,numeric986,numeric987,numeric988,numeric989,numeric990,numeric991,numeric992,numeric993,numeric994,numeric995,numeric996,numeric997,numeric998,numeric999,numeric1000, numeric1001,numeric1002,numeric1003,numeric1004,numeric1005,numeric1006,numeric1007,numeric1008,numeric1009,numeric1010,numeric1011,numeric1012,numeric1013,numeric1014,numeric1015,numeric1016,numeric1017,numeric1018,numeric1019,numeric1020,numeric1021,numeric1022,numeric1023,numeric1024,numeric1025,numeric1026,numeric1027,numeric1028,numeric1029,numeric1030,numeric1031,numeric1032,numeric1033,numeric1034,numeric1035,numeric1036,numeric1037,numeric1038,numeric1039,numeric1040,numeric1041,numeric1042,numeric1043,numeric1044,numeric1045,numeric1046,numeric1047,numeric1048,numeric1049,numeric1050,decimal1,decimal2,decimal3,decimal4,decimal5,decimal6,decimal7,decimal8,decimal9,decimal10,decimal11,decimal12,decimal13,decimal14,decimal15,decimal16,decimal17,decimal18,decimal19,decimal20,decimal21,decimal22,decimal23,decimal24,decimal25,decimal26,decimal27,decimal28,decimal29,decimal30,string1,string2,string3,string4,string5,string6,string7,string8,string9,string10,string11,string12,string13,string14,string15,string16,string17,string18,string19,string20,string21,string22,string23,string24,string25,string26,string27,string28,string29,string30) FROM LOCAL '"+fullFilePath+"' DIRECT TRAILING NULLCOLS REJECTED DATA '"+rejectedFullFilePath+"'";
           // String command = "COPY fact_storm (network_sid,date_sid,date_id,hour_sid,second_sid,audit_sid AS "+ auditSid+ ", counter1,numeric1,counter2,numeric2,counter3,numeric3,counter4,numeric4,counter5,numeric5,counter6,numeric6,counter7,numeric7,counter8,numeric8,counter9,numeric9,counter10,numeric10,counter11,numeric11,counter12,numeric12,counter13,numeric13,counter14,numeric14,counter15,numeric15,counter16,numeric16,counter17,numeric17,counter18,numeric18,counter19,numeric19,counter20,numeric20,counter21,numeric21,counter22,numeric22,counter23,numeric23,counter24,numeric24,counter25,numeric25,counter26,numeric26,counter27,numeric27,counter28,numeric28,counter29,numeric29,counter30,numeric30,numeric61,numeric62,numeric63,numeric64,numeric65,numeric66,numeric67,numeric68,numeric69,numeric70,numeric71,numeric72,numeric73,numeric74,numeric75,numeric76,numeric77,numeric78,numeric79,numeric80,numeric81,numeric82,numeric83,numeric84,numeric85,numeric86,numeric87,numeric88,numeric89,numeric90,numeric91,numeric92,numeric93,numeric94,numeric95,numeric96,numeric97,numeric98,numeric99,numeric100,numeric101,numeric102,numeric103,numeric104,numeric105,numeric106,numeric107,numeric108,numeric109,numeric110,numeric111,numeric112,numeric113,numeric114,numeric115,numeric116,numeric117,numeric118,numeric119,numeric120,numeric121,numeric122,numeric123,numeric124,numeric125,numeric126,numeric127,numeric128,numeric129,numeric130,numeric131,numeric132,numeric133,numeric134,numeric135,numeric136,numeric137,numeric138,numeric139,numeric140,numeric141,numeric142,numeric143,numeric144,numeric145,numeric146,numeric147,numeric148,numeric149,numeric150,numeric151,numeric152,numeric153,numeric154,numeric155,numeric156,numeric157,numeric158,numeric159,numeric160,numeric161,numeric162,numeric163,numeric164,numeric165,numeric166,numeric167,numeric168,numeric169,numeric170,numeric171,numeric172,numeric173,numeric174,numeric175,numeric176,numeric177,numeric178,numeric179,numeric180,numeric181,numeric182,numeric183,numeric184,numeric185,numeric186,numeric187,numeric188,numeric189,numeric190,numeric191,numeric192,numeric193,numeric194,numeric195,numeric196,numeric197,numeric198,numeric199,numeric200,numeric201,numeric202,numeric203,numeric204,numeric205,numeric206,numeric207,numeric208,numeric209,numeric210,numeric211,numeric212,numeric213,numeric214,numeric215,numeric216,numeric217,numeric218,numeric219,numeric220,numeric221,numeric222,numeric223,numeric224,numeric225,numeric226,numeric227,numeric228,numeric229,numeric230,numeric231,numeric232,numeric233,numeric234,numeric235,numeric236,numeric237,numeric238,numeric239,numeric240,numeric241,numeric242,numeric243,numeric244,numeric245,numeric246,numeric247,numeric248,numeric249,numeric250,numeric251,numeric252,numeric253,numeric254,numeric255,numeric256,numeric257,numeric258,numeric259,numeric260,numeric261,numeric262,numeric263,numeric264,numeric265,numeric266,numeric267,numeric268,numeric269,numeric270,numeric271,numeric272,numeric273,numeric274,numeric275,numeric276,numeric277,numeric278,numeric279,numeric280,numeric281,numeric282,numeric283,numeric284,numeric285,numeric286,numeric287,numeric288,numeric289,numeric290,numeric291,numeric292,numeric293,numeric294,numeric295,numeric296,numeric297,numeric298,numeric299,numeric300,numeric301,numeric302,numeric303,numeric304,numeric305,numeric306,numeric307,numeric308,numeric309,numeric310,numeric311,numeric312,numeric313,numeric314,numeric315,numeric316,numeric317,numeric318,numeric319,numeric320,numeric321,numeric322,numeric323,numeric324,numeric325,numeric326,numeric327,numeric328,numeric329,numeric330,numeric331,numeric332,numeric333,numeric334,numeric335,numeric336,numeric337,numeric338,numeric339,numeric340,numeric341,numeric342,numeric343,numeric344,numeric345,numeric346,numeric347,numeric348,numeric349,numeric350,numeric351,numeric352,numeric353,numeric354,numeric355,numeric356,numeric357,numeric358,numeric359,numeric360,numeric361,numeric362,numeric363,numeric364,numeric365,numeric366,numeric367,numeric368,numeric369,numeric370,numeric371,numeric372,numeric373,numeric374,numeric375,numeric376,numeric377,numeric378,numeric379,numeric380,numeric381,numeric382,numeric383,numeric384,numeric385,numeric386,numeric387,numeric388,numeric389,numeric390,numeric391,numeric392,numeric393,numeric394,numeric395,numeric396,numeric397,numeric398,numeric399,numeric400,numeric401,numeric402,numeric403,numeric404,numeric405,numeric406,numeric407,numeric408,numeric409,numeric410,numeric411,numeric412,numeric413,numeric414,numeric415,numeric416,numeric417,numeric418,numeric419,numeric420,numeric421,numeric422,numeric423,numeric424,numeric425,numeric426,numeric427,numeric428,numeric429,numeric430,numeric431,numeric432,numeric433,numeric434,numeric435,numeric436,numeric437,numeric438,numeric439,numeric440,numeric441,numeric442,numeric443,numeric444,numeric445,numeric446,numeric447,numeric448,numeric449,numeric450,numeric451,numeric452,numeric453,numeric454,numeric455,numeric456,numeric457,numeric458,numeric459,numeric460,numeric461,numeric462,numeric463,numeric464,numeric465,numeric466,numeric467,numeric468,numeric469,numeric470,numeric471,numeric472,numeric473,numeric474,numeric475,numeric476,numeric477,numeric478,numeric479,numeric480,numeric481,numeric482,numeric483,numeric484,numeric485,numeric486,numeric487,numeric488,numeric489,numeric490,numeric491,numeric492,numeric493,numeric494,numeric495,numeric496,numeric497,numeric498,numeric499,numeric500,numeric501,numeric502,numeric503,numeric504,numeric505,numeric506,numeric507,numeric508,numeric509,numeric510,numeric511,numeric512,numeric513,numeric514,numeric515,numeric516,numeric517,numeric518,numeric519,numeric520,numeric521,numeric522,numeric523,numeric524,numeric525,numeric526,numeric527,numeric528,numeric529,numeric530,numeric531,numeric532,numeric533,numeric534,numeric535,numeric536,numeric537,numeric538,numeric539,numeric540,numeric541,numeric542,numeric543,numeric544,numeric545,numeric546,numeric547,numeric548,numeric549,numeric550,numeric551,numeric552,numeric553,numeric554,numeric555,numeric556,numeric557,numeric558,numeric559,numeric560,numeric561,numeric562,numeric563,numeric564,numeric565,numeric566,numeric567,numeric568,numeric569,numeric570,numeric571,numeric572,numeric573,numeric574,numeric575,numeric576,numeric577,numeric578,numeric579,numeric580,numeric581,numeric582,numeric583,numeric584,numeric585,numeric586,numeric587,numeric588,numeric589,numeric590,numeric591,numeric592,numeric593,numeric594,numeric595,numeric596,numeric597,numeric598,numeric599,numeric600,numeric601,numeric602,numeric603,numeric604,numeric605,numeric606,numeric607,numeric608,numeric609,numeric610,numeric611,numeric612,numeric613,numeric614,numeric615,numeric616,numeric617,numeric618,numeric619,numeric620,numeric621,numeric622,numeric623,numeric624,numeric625,numeric626,numeric627,numeric628,numeric629,numeric630,numeric631,numeric632,numeric633,numeric634,numeric635,numeric636,numeric637,numeric638,numeric639,numeric640,numeric641,numeric642,numeric643,numeric644,numeric645,numeric646,numeric647,numeric648,numeric649,numeric650,numeric651,numeric652,numeric653,numeric654,numeric655,numeric656,numeric657,numeric658,numeric659,numeric660,numeric661,numeric662,numeric663,numeric664,numeric665,numeric666,numeric667,numeric668,numeric669,numeric670,numeric671,numeric672,numeric673,numeric674,numeric675,numeric676,numeric677,numeric678,numeric679,numeric680,numeric681,numeric682,numeric683,numeric684,numeric685,numeric686,numeric687,numeric688,numeric689,numeric690,numeric691,numeric692,numeric693,numeric694,numeric695,numeric696,numeric697,numeric698,numeric699,numeric700,numeric701,numeric702,numeric703,numeric704,numeric705,numeric706,numeric707,numeric708,numeric709,numeric710,numeric711,numeric712,numeric713,numeric714,numeric715,numeric716,numeric717,numeric718,numeric719,numeric720,numeric721,numeric722,numeric723,numeric724,numeric725,numeric726,numeric727,numeric728,numeric729,numeric730,numeric731,numeric732,numeric733,numeric734,numeric735,numeric736,numeric737,numeric738,numeric739,numeric740,numeric741,numeric742,numeric743,numeric744,numeric745,numeric746,numeric747,numeric748,numeric749,numeric750,numeric751,numeric752,numeric753,numeric754,numeric755,numeric756,numeric757,numeric758,numeric759,numeric760,numeric761,numeric762,numeric763,numeric764,numeric765,numeric766,numeric767,numeric768,numeric769,numeric770,numeric771,numeric772,numeric773,numeric774,numeric775,numeric776,numeric777,numeric778,numeric779,numeric780,numeric781,numeric782,numeric783,numeric784,numeric785,numeric786,numeric787,numeric788,numeric789,numeric790,numeric791,numeric792,numeric793,numeric794,numeric795,numeric796,numeric797,numeric798,numeric799,numeric800,numeric801,numeric802,numeric803,numeric804,numeric805,numeric806,numeric807,numeric808,numeric809,numeric810,numeric811,numeric812,numeric813,numeric814,numeric815,numeric816,numeric817,numeric818,numeric819,numeric820,numeric821,numeric822,numeric823,numeric824,numeric825,numeric826,numeric827,numeric828,numeric829,numeric830,numeric831,numeric832,numeric833,numeric834,numeric835,numeric836,numeric837,numeric838,numeric839,numeric840,numeric841,numeric842,numeric843,numeric844,numeric845,numeric846,numeric847,numeric848,numeric849,numeric850,numeric851,numeric852,numeric853,numeric854,numeric855,numeric856,numeric857,numeric858,numeric859,numeric860,numeric861,numeric862,numeric863,numeric864,numeric865,numeric866,numeric867,numeric868,numeric869,numeric870,numeric871,numeric872,numeric873,numeric874,numeric875,numeric876,numeric877,numeric878,numeric879,numeric880,numeric881,numeric882,numeric883,numeric884,numeric885,numeric886,numeric887,numeric888,numeric889,numeric890,numeric891,numeric892,numeric893,numeric894,numeric895,numeric896,numeric897,numeric898,numeric899,numeric900,numeric901,numeric902,numeric903,numeric904,numeric905,numeric906,numeric907,numeric908,numeric909,numeric910,numeric911,numeric912,numeric913,numeric914,numeric915,numeric916,numeric917,numeric918,numeric919,numeric920,numeric921,numeric922,numeric923,numeric924,numeric925,numeric926,numeric927,numeric928,numeric929,numeric930,numeric931,numeric932,numeric933,numeric934,numeric935,numeric936,numeric937,numeric938,numeric939,numeric940,numeric941,numeric942,numeric943,numeric944,numeric945,numeric946,numeric947,numeric948,numeric949,numeric950,numeric951,numeric952,numeric953,numeric954,numeric955,numeric956,numeric957,numeric958,numeric959,numeric960,numeric961,numeric962,numeric963,numeric964,numeric965,numeric966,numeric967,numeric968,numeric969,numeric970,numeric971,numeric972,numeric973,numeric974,numeric975,numeric976,numeric977,numeric978,numeric979,numeric980,numeric981,numeric982,numeric983,numeric984,numeric985,numeric986,numeric987,numeric988,numeric989,numeric990,numeric991,numeric992,numeric993,numeric994,numeric995,numeric996,numeric997,numeric998,numeric999,numeric1000,numeric1001,numeric1002,numeric1003,numeric1004,numeric1005,numeric1006,numeric1007,numeric1008,numeric1009,numeric1010,numeric1011,numeric1012,numeric1013,numeric1014,numeric1015,numeric1016,numeric1017,numeric1018,numeric1019,numeric1020,numeric1021,numeric1022,numeric1023,numeric1024,numeric1025,numeric1026,numeric1027,numeric1028,numeric1029,numeric1030,numeric1031,numeric1032,numeric1033,numeric1034,numeric1035,numeric1036,numeric1037,numeric1038,numeric1039,numeric1040,numeric1041,numeric1042,numeric1043,numeric1044,numeric1045,numeric1046,numeric1047,numeric1048,numeric1049,numeric1050,decimal1,decimal2,decimal3,decimal4,decimal5,decimal6,decimal7,decimal8,decimal9,decimal10,decimal11,decimal12,decimal13,decimal14,decimal15,decimal16,decimal17,decimal18,decimal19,decimal20,decimal21,decimal22,decimal23,decimal24,decimal25,decimal26,decimal27,decimal28,decimal29,decimal30,string1,string2,string3,string4,string5,string6,string7,string8,string9,string10,string11,string12,string13,string14,string15,string16,string17,string18,string19,string20,string21,string22,string23,string24,string25,string26,string27,string28,string29,string30) FROM LOCAL '"+fullFilePath+"' TRAILING NULLCOLS";

            LOGGER.warn("Executing: "+command);
            stmt.execute(command);

            LOGGER.warn("COPY Statement executed successfully... {} were loaded", stmt.getUpdateCount());

            HbA.updateAudit(auditSid, String.valueOf(stmt.getUpdateCount()), String.valueOf(countLines(fullFilePath)- stmt.getUpdateCount()), "Processed");

            String query = " select count (DISTINCT audit_sid) cnt, hour_sid\n" +
                    " from storm_stg_fact\n" +
                    " WHERE hour_sid = "+hourSid+"\n" +
                    " group by hour_sid;";

            Integer filesCount=0;
            ResultSet rs = stmt.executeQuery(query);
            while ( rs.next() ) {
                filesCount = Integer.valueOf(rs.getString("cnt"));
            };

            if (filesCount == 5) {
                // Initialize object for ScripRunner
                LOGGER.warn("Initializing object for ScripRunner...");
                ScriptRunner sr = new ScriptRunner(conn, false, false);

                // Give the input file to Reader
                LOGGER.warn("Adding the input file to Reader");
                Reader reader = new BufferedReader(
                        new FileReader(populateNetworkAndDictionary));

                // Exctute script
                LOGGER.warn("Executing Script to populate Storm_Dim_Network and Dictionary Tables");
                sr.runScript(reader);

                conn.commit();
            }

        } catch (SQLException e) {
            LOGGER.error("Error executing copy statement... {}" , e.getMessage());
                    } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void cleanup(){
        try {
            conn.close();
            LOGGER.warn("Connection to Vertica is Closed");
        } catch (SQLException e) {
            LOGGER.error("Error closing connection to Vertica {}" , e.getMessage());
        }
    }

    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

}
