use wrd10_socialmedia;
add jar /usr/lib/hive/auxlib/json-serde-1.1.9.9-Hive13-jar-with-dependencies.jar;
add jar /home/sm_user/SentimentAnalysis-1.0.jar;
add file /home/sm_user/classifier.txt;
create temporary function getPolarity as 'com.sentiment.GetPolarity';
create temporary function getEntitySubject as 'com.sentiment.GetEntitySubject';

with posts as ( 
	select timestamp_ms,
	page.id as company_id,
	page.name as company_name,
	page.likes as company_page_likes_num,
	id as post_id,
	fr0m.id as post_author_id,
	created_time as post_created_time,
	size(comments.data) as post_comments_num,
	size(likes.data) as post_likes_num,
	comments,
	country,
	month_key 
	from stg_raw_facebook_pages pages_raw
	where country='Lebanon' and month_key='201504'
), 
latest_posts as (
	select p1.* from posts p1 join (
	SELECT max(cast(p2.timestamp_ms as double)) AS max_ts,p2.post_id
	FROM posts p2 group by p2.post_id) p3 on p1.timestamp_ms = p3.max_ts and p1.post_id = p3.post_id
)
insert into table fact_orc_raw_facebook_post_comments partition(country,month_key)
select 
	lp.timestamp_ms,
	lp.company_id,
	lp.company_name,
	lp.company_page_likes_num,
	lp.post_id,
	lp.post_author_id,
	lp.post_created_time,
	lp.post_likes_num,
	lp.post_comments_num,
	comment.id as comment_id,
	comment.created_time as comment_created_time,
	comment.message as comment_message,
	comment.fr0m.id as comment_author_id,
	comment.fr0m.name as comment_author_name,
	comment.parent.id as comment_parent_id,
	comment.like_count as comment_likes_num,
	getPolarity(comment.message) as comment_sentiment,
	getEntitySubject(comment.message) as comment_sentiment_category,
	'' as topic,
	lp.country,
	lp.month_key
from latest_posts lp
LATERAL VIEW outer explode(comments.data) comments AS comment;