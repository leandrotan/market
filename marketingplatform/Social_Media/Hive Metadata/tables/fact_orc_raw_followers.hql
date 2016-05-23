create table fact_orc_raw_followers (
 social_media_source string,
 day_key string,
 created_at string,
 csp string,
 twitter_followers_count int,
 facebook_page_likes_count int,
 unique_id string
) PARTITIONED BY (country String) 
STORED AS ORC;