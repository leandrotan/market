create or replace view report_view_benchmark_sentiment_trend as
select sum(t1.comments_count) as comments_count,
t1.day_key,
t1.csp,
t1.social_media_source,
t1.country,
t1.sentiment
from wrd10_socialmedia.fact_view_daily_social_media t1
where day_key!='20150420'
group by t1.day_key,
t1.csp,
t1.social_media_source,
t1.country,
t1.sentiment
union all
select sum(t2.comments_count) as comments_count,
t2.day_key,
t2.csp,
t2.social_media_source,
t2.country,
null as sentiment
from wrd10_socialmedia.fact_view_daily_social_media t2
where day_key!='20150420'
group by t2.day_key,
t2.csp,
t2.social_media_source,
t2.country