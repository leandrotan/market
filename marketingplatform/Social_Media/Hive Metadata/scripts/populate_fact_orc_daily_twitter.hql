insert overwrite table fact_orc_daily_twitter partition(country,day_key) 
   select count(distinct id) as tweet_count,
   mention_screen_name,
   country_full_name,
   sentiment_category as sentiment,
   split(catsub,' / ')[0] as category, 
   case when split(catsub,' / ')[0] = 'Customer Care' then 'Customer Care' else split(catsub,' / ')[1] end as subcategory,
   country,
   day_key
  from  fact_orc_raw_tweets 
  LATERAL VIEW outer explode(split(entity_subject,' \\| ')) categories AS catsub where country <> 'United States'
  -- where day_key = from_unixtime(unix_timestamp()-1*60*60*24, 'yyyyMMdd') -- comment this line if you want to insert data more than yesterday
 group by mention_screen_name, country_full_name, sentiment_category, split(catsub,' / ')[0], 
   case when split(catsub,' / ')[0] = 'Customer Care' then 'Customer Care' else split(catsub,' / ')[1] end , country, day_key;