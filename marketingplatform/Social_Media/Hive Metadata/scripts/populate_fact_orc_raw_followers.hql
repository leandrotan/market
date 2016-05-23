insert into table fact_orc_raw_followers partition (country)
select 
'Twitter' as social_media_source,
 f.day_key,
 from_unixtime(unix_timestamp(to_utc_timestamp(unix_timestamp(f.created_at,"EEE MMM dd hh:mm:ss Z yyyy")*1000,"EST")),'dd-MMM-yyyy hh:mm:ss') as created_at,
 o.csp,
 user.followers_count as twitter_followers_count,
 -1 as facebook_page_likes_count,
 f.id as unique_id,
 o.country
from stg_raw_twitter_userstream f
join dim_operator o on f.user.screen_name = o.csp_screen_name
where o.country = 'Egypt';

insert into table fact_orc_raw_followers partition (country='Egypt')
select 
'Twitter' as social_media_source,
 f.day_key,
 from_unixtime(unix_timestamp(to_utc_timestamp(unix_timestamp(f.created_at,"EEE MMM dd hh:mm:ss Z yyyy")*1000,"EST")),'dd-MMM-yyyy hh:mm:ss') as created_at,
 o.csp,
 user.followers_count as twitter_followers_count,
 -1 as facebook_page_likes_count,
 f.id as unique_id
from stg_raw_twitter f
join dim_operator o on f.user.screen_name = o.csp_screen_name and f.country=o.country
where f.country = 'Egypt';

insert into table fact_orc_raw_followers partition (country)
select 
social_media_source,
from_unixtime(cast(timestamp_ms/1000 as bigint),'yyyyMMdd') as day_key,
from_unixtime(cast(timestamp_ms/1000 as bigint),'dd-MMM-yyyy hh:mm:ss') as created_at,
csp,
twitter_followers_count,
facebook_page_likes_count,
timestamp_ms as unique_id,
country
from (
select distinct
'Facebook' as social_media_source,
f1.country,
o.csp,
-1 as twitter_followers_count,
f1.page.likes as facebook_page_likes_count,
f1.timestamp_ms
from stg_raw_facebook_pages f1
join dim_operator o on f1.page.id = o.csp_screen_name and o.country= f1.country
where f1.country = 'Egypt' and f1.timestamp_ms is not null) tt