create table fact_orc_twitter_conversations (
 child_tweet_id BIGINT,
 child_user_name STRING,
 child_text STRING,
 child_created_at STRING,
 parent_tweet_id BIGINT,
 root_tweet_id BIGINT,
 level INT
)
PARTITIONED BY (country String) 
STORED AS ORC
TBLPROPERTIES ("transactional"="true","NO_AUTO_COMPACTION"="false");