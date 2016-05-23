create table fact_orc_raw_facebook_page_posts (
	timestamp_ms 			String,
	company_id 				String,
	company_name 			String,
	company_page_likes_num 	Int,
	post_id 				String,
	post_author_id 			String,
	post_author_name 		String,
	post_author_category 	String,
	post_author_region		String,
	post_message 			String,
	post_created_time 		String,
	post_to 				String,
	post_comments_num 		Int,
	post_likes_num 			Int,
	sentiment				String,
	sentiment_category		String,
	Topic		            String
) PARTITIONED BY (country String, month_key String) 
STORED AS ORC;