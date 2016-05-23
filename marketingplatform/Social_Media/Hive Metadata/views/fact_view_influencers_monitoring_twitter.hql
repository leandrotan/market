Create view fact_view_influencers_monitoring_twitter As(
SELECT t.created_at as Created_at, 
  id, t.user_name, 
  followers, 
  sentiment_category, 
  t.country, 
  to_date(FROM_UNIXTIME( unix_timestamp(day_key,"yyyyMMdd"),"yyyy-MM-dd")) as day_key, 
  o.csp
from fact_orc_raw_tweets t
inner join dim_operator o on lower(o.country)=lower(t.country) and lower(o.csp_screen_name)= lower(t.mention_screen_name));