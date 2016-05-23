create table fact_orc_daily_twitter (
	tweet_count BIGINT,
	mention_screen_name STRING,
	country_full_name STRING,
	sentiment STRING,
	category STRING,
	subcategory STRING
)
PARTITIONED BY (country String, day_key String) 
STORED AS ORC;