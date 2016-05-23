insert into table fact_orc_twitter_conversations partition(country='Egypt')
select 
 child_tweet_id,
 child_user_name,
 child_text,
 child_created_at,
 parent_tweet_id,
 root_tweet_id,
 level
from stg_twitter_conversations
where country = 'Egypt'