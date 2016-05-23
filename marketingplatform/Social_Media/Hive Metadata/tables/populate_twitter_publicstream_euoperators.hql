insert into table twitter_publicstream_euoperators PARTITION (DAY_KEY)
select o.* from twitter_publicstream_eu_orc o
LATERAL VIEW explode(entities.hashtags) contained_hashtags AS hashtags
where lower(hashtags.text) = 'vodafoneuk' 
or lower(hashtags.text) = 'virginmedia' 
or lower(hashtags.text) = 'vodafoneukhelp' 
or lower(hashtags.text) = 'orangeuk' 
or lower(hashtags.text)= 'threeuk' 
or lower(hashtags.text) = 'o2' 
or lower(hashtags.text) = 'tmobileuk' 
or lower(hashtags.text) = 'threeuksupport' 
or lower(hashtags.text) = 'ee' 
UNION all 
select o.* from twitter_publicstream_eu_orc o
LATERAL VIEW explode(entities.user_mentions) contained_mentions AS mentions
where lower(mentions.screen_name) = 'vodafoneuk' 
or lower(mentions.screen_name) = 'virginmedia' 
or lower(mentions.screen_name)  = 'vodafoneukhelp' 
or lower(mentions.screen_name)  = 'orangeuk' 
or lower(mentions.screen_name)  = 'threeuk' 
or lower(mentions.screen_name)  = 'o2' 
or lower(mentions.screen_name)  = 'tmobileuk' 
or lower(mentions.screen_name)  = 'threeuksupport' 
or lower(mentions.screen_name)  = 'ee';

insert overwrite table twitter_publicstream_euoperators partition(day_key) select distinct
coordinates,retweeted,source,matched_keywords,entities,favorite_count,in_reply_to_status_id_str,geo,id_str,in_reply_to_user_id,timestamp_ms,truncated,text,retweet_count,retweeted_status,id,in_reply_to_status_id,possibly_sensitive,filter_level,created_at,place,favorited,lang,contributors,in_reply_to_screen_name,in_reply_to_user_id_str,user,day_key
from twitter_publicstream_euoperators;