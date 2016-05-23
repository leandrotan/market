create or replace view fact_view_raw_followers as
select 
 social_media_source,
 country,
 day_key,
 created_at,
 csp,
 twitter_followers_count,
 prev_id_twitter_followers_count,
 facebook_page_likes_count,
 prev_id_facebook_page_likes_count,
 twitter_followers_count - prev_id_twitter_followers_count as twitter_new_followers,
 case when (twitter_followers_count - prev_id_twitter_followers_count) < 0 then (twitter_followers_count - prev_id_twitter_followers_count)*-1 else null end as twitter_unfollowers,
 facebook_page_likes_count - prev_id_facebook_page_likes_count as facebook_new_followers,
 case when (facebook_page_likes_count - prev_id_facebook_page_likes_count) < 0 then (facebook_page_likes_count - prev_id_facebook_page_likes_count)*-1 else null end as facebook_unfollowers,
 unique_id
from (
select 
 social_media_source,
 country,
 day_key,
 created_at,
 csp,
 case when twitter_followers_count = -1 then null else twitter_followers_count end as twitter_followers_count,
 case when facebook_page_likes_count = -1 then null else facebook_page_likes_count end as facebook_page_likes_count,
 lag(case when twitter_followers_count = -1 then null else twitter_followers_count end) over (partition by country,social_media_source,csp order by day_key,unique_id,unix_timestamp(created_at,'dd-MMM-yyyy hh:mm:ss') asc) as prev_id_twitter_followers_count,
 lag(case when facebook_page_likes_count = -1 then null else facebook_page_likes_count end) over (partition by country,social_media_source,csp order by day_key,unique_id,unix_timestamp(created_at,'dd-MMM-yyyy hh:mm:ss') asc) as prev_id_facebook_page_likes_count,
 unique_id
from 

(
select 
 t.social_media_source,
 t.country,
 t.day_key,
 t.created_at,
 t.csp,
 nvl(t.twitter_followers_count,t.last_twitter_followers_count + case when t.social_media_source = 'Twitter' then t.twitter_empty_group_num*400 else 0 end) as twitter_followers_count,
 nvl(t.facebook_page_likes_count,t.last_facebook_page_likes_count + case when t.social_media_source = 'Facebook' then t.facebook_empty_group_num*1000 else 0 end) as facebook_page_likes_count,
 t.unique_id
from 
(
select 
 tt.social_media_source,
 tt.country,
 tt.day_key,
 tt.created_at,
 tt.csp,
 tt.twitter_followers_count,
 tt.facebook_page_likes_count,
 tt.unique_id,
 tt.last_twitter_followers_count,
 tt.last_facebook_page_likes_count,
 row_number() over (partition by tt.social_media_source,tt.country,tt.csp,tt.last_twitter_followers_count order by tt.day_key) as twitter_empty_group_num,
 row_number() over (partition by tt.social_media_source,tt.country,tt.csp,tt.last_facebook_page_likes_count order by tt.day_key) as facebook_empty_group_num
from (
select d.social_media_source,
 d.country,
 d.day_key,
 nvl(f.created_at,from_unixtime(unix_timestamp(d.day_key,'yyyyMMdd'),'dd-MMM-yyyy hh:mm:ss')) as created_at,
 d.csp,
 f.twitter_followers_count,
 f.facebook_page_likes_count,
 nvl(f.unique_id,d.day_key) as unique_id,
 max(twitter_followers_count) over (partition by d.social_media_source,d.country,d.csp order by d.day_key) as last_twitter_followers_count,
 max(facebook_page_likes_count) over (partition by d.social_media_source,d.country,d.csp order by d.day_key) as last_facebook_page_likes_count
from tmp_dim_date_csp_wrd332 d
left join fact_orc_raw_followers f
on f.day_key = d.day_key and f.csp = d.csp and f.social_media_source=d.social_media_source and f.country=d.country) tt
) t
) rr

) f;