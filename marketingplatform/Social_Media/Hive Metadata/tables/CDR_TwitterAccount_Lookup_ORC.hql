CREATE TABLE IF NOT EXISTS CDR_TwitterAccount_Lookup_orc
(MSISDN STRING,
 IMSI BIGINT,
 Twitter_ID STRING,
 user_name STRING,
 user_description STRING,
 user_location STRING,
 user_profile_location STRING,
 user_timezone STRING,
 Name STRING,
 Email STRING,
 Nationality STRING,
 Sex STRING,
 DateOfBirth STRING,
 Contract_Start_Day STRING,
 Contract_End_Day STRING
 )
    ROW FORMAT DELIMITED
    stored as ORC;