create or replace view fact_view_daily_social_media as
   select 
 'Twitter' as social_media_source,
 f.day_key,
 f.country,
 op.csp,
 f.sentiment,
 f.category,
 f.subcategory,
 null as company_page_likes_num,
 f.country_full_name, 
 cdr.region,
 f.tweet_count as comments_count,
 null as comments2comments_count,
 null as likes2comments_count
 from fact_orc_daily_twitter f
  left join wrd10_socialmedia.dim_cityregion cdr on upper(trim(cdr.location)) = upper(trim(f.country_full_name)), 
  wrd10_socialmedia.dim_operator op
 where upper(trim(op.csp_screen_name)) = upper(trim(f.mention_screen_name)) and f.country = op.country
union all
 select
 'Facebook' as social_media_source,
 f.day_key,
 f.country,
 o.csp,
 f.sentiment,
 f.sentiment_category as category,
 f.sentiment_subcategory as subcategory,
 f.company_page_likes_num,
 null as country_full_name,
 f.author_region as region,
 f.posts_count as comments_count,
 f.post_comments_num as comments2comments_count,
 f.post_likes_num as likes2comments_count
 from fact_orc_daily_facebook_page_posts f
 join dim_operator o on f.company_id = o.csp_screen_name and f.country = o.country
union all
 select
 'Facebook' as social_media_source,
 f1.day_key,
 f1.country,
 o.csp,
 f1.comment_sentiment as sentiment,
 f1.sentiment_category as category,
 f1.sentiment_subcategory as subcategory,
 f1.company_page_likes_num,
 null as country_full_name,
 null as region,
 f1.comments_count,
 0 as comments2comments_count,
 f1.comment_likes_num as likes2comments_count
 from fact_orc_daily_facebook_post_comments f1
 join dim_operator o on f1.company_id = o.csp_screen_name and f1.country = o.country;