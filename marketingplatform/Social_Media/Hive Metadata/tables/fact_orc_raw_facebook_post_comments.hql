create table fact_orc_raw_facebook_post_comments (
	timestamp_ms 			String,
	company_id 				String,
	company_name 			String,
	company_page_likes_num	Int,
	post_id 				String,
	post_author_id			String,
	post_created_time 		String,
	post_likes_num 			Int,
	post_comments_num		Int,
	comment_id				String,
	comment_created_time	String,
	comment_message			String,
	comment_author_id		String,
	comment_author_name		String,
	comment_parent_id		String,
	comment_likes_num		String,
	comment_sentiment		String,
	comment_sentiment_category	String,
	topic		            String
) PARTITIONED BY (country String, month_key String) 
STORED AS ORC;