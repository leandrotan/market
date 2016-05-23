create or replace view fact_view_monthly_followers as
select 
 country,
 social_media_source,
 csp,
 concat(substr(day_key,1,6),"01") as month_key,
 max(twitter_followers_count) as twitter_followers_count,
 max(facebook_page_likes_count) as facebook_page_likes_count,
 lag(max(twitter_followers_count)) over (partition by csp,social_media_source order by substr(day_key,1,6) asc) as prev_month_twitter_followers_count,
 lag(max(facebook_page_likes_count)) over (partition by csp,social_media_source order by substr(day_key,1,6) asc) as prev_month_facebook_page_likes_count,
 sum(twitter_new_followers) as twitter_new_followers,
 sum(twitter_unfollowers) as twitter_unfollowers,
 sum(facebook_new_followers) as facebook_new_followers,
 sum(facebook_unfollowers) as facebook_unfollowers
from fact_view_raw_followers
group by substr(day_key,1,6),csp,country,social_media_source