create table fact_orc_raw_tweets (
 timestamp_ms STRING,
 created_at STRING,
 id BIGINT,
 text STRING,
 hashtags STRING,
 mention_screen_name STRING,
 coordinates STRING,
 source STRING,
 lang STRING,
 user_id BIGINT,
 user_name STRING,
 user_screen_name STRING,
 user_location STRING,
 followers INT,
 in_reply_to_user_id BIGINT,
 in_reply_to_screen_name STRING,
 in_reply_to_status_id BIGINT,
 country_full_name STRING,
 country_name STRING,
 sentiment_category STRING,
 entity_subject STRING,
 topic STRING
)
PARTITIONED BY (country String, day_key String) 
STORED AS ORC;