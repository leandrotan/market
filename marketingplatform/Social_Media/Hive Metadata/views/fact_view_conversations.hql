create or replace view fact_view_conversations as
select 
 'Facebook' as social_media_channel,
 f2.child_message_id,
 f2.child_message,
 f2.child_user_name,
 from_unixtime(unix_timestamp(to_utc_timestamp(unix_timestamp(f2.child_created_at,"yyyy-MM-dd'T'hh:mm:ssZ")*1000,"EST")),'dd-MMM-yyyy hh:mm:ss') as child_created_at,
 f2.root_message_id,
 f2.level,
 f2.country
from fact_orc_facebook_conversations f2
where 1 = 1
union all
select 
 'Twitter' as social_media_channel,
 c1.child_tweet_id as child_message_id,
 c1.child_text as child_message,
 c1.child_user_name as child_user_name,
 c1.child_created_at,
 c1.root_tweet_id as root_message_id,
 c1.level,
 c1.country
from (
select 
 child_tweet_id,
 child_text,
 child_user_name,
 from_unixtime(unix_timestamp(to_utc_timestamp(unix_timestamp(child_created_at,"EEE MMM dd hh:mm:ss Z yyyy")*1000,"EST")),'dd-MMM-yyyy hh:mm:ss') as child_created_at,
 root_tweet_id,
 level,
 country
from fact_orc_twitter_conversations
union all
select distinct
 cc.root_tweet_id as child_tweet_id,
 t.text as child_text,
 t.user_name as child_user_name,
 from_unixtime(unix_timestamp(to_utc_timestamp(unix_timestamp(t.created_at,"EEE MMM dd hh:mm:ss Z yyyy")*1000,"EST")),'dd-MMM-yyyy hh:mm:ss') as child_created_at,
 root_tweet_id,
 0 as level,
 cc.country
from fact_orc_twitter_conversations cc
join fact_orc_raw_tweets t on t.id = cc.root_tweet_id and t.country = cc.country
) c1;