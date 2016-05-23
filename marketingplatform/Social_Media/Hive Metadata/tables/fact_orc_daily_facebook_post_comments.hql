create table fact_orc_daily_facebook_post_comments (
 day_key String,
 company_id String,
 company_name String,
 comment_sentiment String,
 sentiment_category String,
 sentiment_subcategory String,
 company_page_likes_num Int,
 author_region String,
 comments_count Int,
 comment_likes_num  Int
) PARTITIONED BY (country String, month_key String) 
STORED AS ORC;