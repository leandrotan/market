create external table stg_twitter_conversations (
 child_tweet_id          string,
 parent_tweet_id         string,
 child_created_at        string,
 child_user_name         string,
 child_text              string,
 root_tweet_id           string,
 level                   int,
 c                       String
)
PARTITIONED BY (country String)
ROW FORMAT DELIMITED FIELDS TERMINATED BY "\t"
 STORED AS TEXTFILE
LOCATION '/user/sm_user/SocialMedia/Conversations';