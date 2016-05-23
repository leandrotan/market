create table fact_orc_facebook_conversations (
 child_message_id STRING,
 child_user_name STRING,
 child_message STRING,
 child_created_at STRING,
 parent_message_id STRING,
 root_message_id STRING,
 level INT
)
PARTITIONED BY (country String) 
STORED AS ORC
TBLPROPERTIES ("transactional"="true","NO_AUTO_COMPACTION"="false");