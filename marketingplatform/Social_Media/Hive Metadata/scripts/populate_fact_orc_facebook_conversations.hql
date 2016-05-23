insert into table fact_orc_facebook_conversations partition(country='Egypt')
select 
 comment_id as child_message_id,
 comment_author_name as child_user_name,
 comment_message as child_message,
 comment_created_time as child_created_at,
 comment_parent_id as parent_message_id,
 post_id as root_message_id,
 case when comment_parent_id is null then 1 else 2 end as level
from fact_orc_raw_facebook_post_comments
where country = 'Egypt';


insert into table fact_orc_facebook_conversations partition(country='Egypt')
select distinct
 c1.post_id as child_message_id,
 p.post_author_name as child_user_name,
 p.post_message as child_message,
 p.post_created_time as child_created_at,
 p.post_id as parent_message_id,
 c1.post_id as root_message_id,
 0 as level
from fact_orc_raw_facebook_post_comments c1
join fact_orc_raw_facebook_page_posts p on p.post_id = c1.post_id and p.country=c1.country
where c1.country = 'Egypt';