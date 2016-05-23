insert overwrite table fact_orc_daily_facebook_page_posts partition (country,month_key)
select 
from_unixtime(unix_timestamp(t1.post_created_time,"yyyy-MM-dd'T'hh:mm:ssZ"),'yyyyMMdd') as day_key,
t1.company_id,
t1.company_name,
t1.sentiment,
t1.category as sentiment_category,
t1.subcategory as sentiment_subcategory,
max(t1.company_page_likes_num) as company_page_likes_num,
t1.post_author_region as author_region,
count(distinct post_id) as posts_count,
sum(t1.post_comments_num) as post_comments_num,
sum(t1.post_likes_num) as post_likes_num,
t1.country,
t1.month_key
from (select f.post_id,f.post_created_time,f.company_id,f.company_name,f.sentiment,f.post_author_region,split(catsub,' / ')[0] as category,
  case when split(catsub,' / ')[0] = 'Customer Care' then 'Customer Care' else split(catsub,' / ')[1] end as subcategory,
  company_page_likes_num,case when post_comments_num = -1 then 0 else post_comments_num end as post_comments_num, case when post_likes_num = -1 then 0 else post_likes_num end as post_likes_num,country,month_key
from fact_orc_raw_facebook_page_posts f
LATERAL VIEW outer explode(split(sentiment_category,' \\| ')) categories AS catsub) t1
group by from_unixtime(unix_timestamp(t1.post_created_time,"yyyy-MM-dd'T'hh:mm:ssZ"),'yyyyMMdd'),
t1.company_id,
t1.company_name,
t1.sentiment,
t1.category,
t1.subcategory,
t1.country,
t1.month_key,
t1.post_author_region