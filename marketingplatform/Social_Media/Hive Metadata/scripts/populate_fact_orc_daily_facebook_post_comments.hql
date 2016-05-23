insert overwrite table fact_orc_daily_facebook_post_comments partition (country,month_key)
select 
from_unixtime(unix_timestamp(t1.comment_created_time,"yyyy-MM-dd'T'hh:mm:ssZ"),'yyyyMMdd') as day_key,
t1.company_id,
t1.company_name,
t1.comment_sentiment,
t1.category as sentiment_category,
t1.subcategory as sentiment_subcategory,
max(t1.company_page_likes_num) as company_page_likes_num,
'N\A' as author_region,
count(distinct comment_id) as comments_count,
sum(t1.comment_likes_num) as comment_likes_num,
t1.country,
t1.month_key
from (select f.comment_id,f.post_id,f.comment_created_time,f.company_id,f.company_name,f.comment_sentiment,split(catsub,' / ')[0] as category,
  case when split(catsub,' / ')[0] = 'Customer Care' then 'Customer Care' else split(catsub,' / ')[1] end as subcategory,
  company_page_likes_num, case when comment_likes_num = -1 then 0 else comment_likes_num end as comment_likes_num,country,month_key
from fact_orc_raw_facebook_post_comments f
LATERAL VIEW outer explode(split(comment_sentiment_category,' \\| ')) categories AS catsub) t1
group by from_unixtime(unix_timestamp(t1.comment_created_time,"yyyy-MM-dd'T'hh:mm:ssZ"),'yyyyMMdd'),
t1.company_id,
t1.company_name,
t1.comment_sentiment,
t1.category,
t1.subcategory,
t1.country,
t1.month_key