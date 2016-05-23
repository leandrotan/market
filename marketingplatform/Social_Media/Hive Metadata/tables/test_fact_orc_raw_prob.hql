CREATE TABLE IF NOT EXISTS test_fact_orc_raw_prob (
        imsi String,
        msisdn String,
        imei String,
        serving_cell_cgi String,
        domain_name String,
        start_time String,
        end_time String
 
)

STORED AS ORC;
