create table fact_orc_daily_facebook_page_posts (
	day_key					String,
	company_id 				String,
	company_name 			String,
	sentiment				String,
	sentiment_category		String,
	sentiment_subcategory	String,
	company_page_likes_num 	Int,
	author_region			String,
	posts_count				Int,
	post_comments_num 		Int,
	post_likes_num 			Int
) PARTITIONED BY (country String, month_key String) 
STORED AS ORC;