DROP TABLE IF EXISTS cdr_data;

CREATE TABLE cdr_data (
        RECORDTYPE String, 
        CALLINGPARTYNUMBER String, 
        CALLINGROAMINFO String, 
        CALLINGCELLID String, 
        CHARGINGTIME String, 
        IMEI String, 
        CALLINGPARTYIMSI String, 
        CHARGINGPARTYNUMBER String, 
        PAYTYPE String, 
        ROAMSTATE String, 
        HOMEZONEID String, 
        SERVICETYPE String, 
        SUBSCRIBERID String, 
        CHARGEOFFUNDACCOUNTS String, 
        CHARGEFROMPREPAID String, 
        PREPAIDBALANCE String, 
        CHARGEFROMPOSTPAID String, 
        POSTPAIDBALANCE String, 
        ACCOUNTID String, 
        CURRENCYCODE String, 
        CALLINGNETWORKTYPE String, 
        GROUPPAYFLAG String, 
        TERMINATIONREASON String, 
        CHARGEDURATION String, 
        CHARGINGID String, 
        DOWNFLUX String, 
        UPFLUX String, 
        SGSNADDRESS String, 
        GGSNADDRESS String, 
        SERVICEID String, 
        CALLINGHOMECOUNTRYCODE String, 
        CALLINGHOMENETWORKCODE String, 
        CALLINGROAMCOUNTRYCODE String, 
        CALLINGROAMNETWORKCODE String, 
        CALLINGVPNGROUPNUMBER String, 
        CALLINGVPNTOPGROUPNUMBER String, 
        CHARGINGTYPE String, 
        DIAMETERSESSIONID String, 
        SERIALNO String, 
        SUBSEQUENCE String
) 
PARTITIONED BY (DAY_KEY String) 
clustered by (IMEI) into 4 buckets
STORED AS ORC;