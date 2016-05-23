create or replace view fact_view_twitter_userstream as
select 
 t.id_str as id,
 t.in_reply_to_status_id_str as in_reply_to_status_id,
 t.coordinates,
 t.source,
 t.text,
 t.created_at,
 t.timestamp_ms,
 t.user.id_str as user_id,
 t.user.name as user_name,
 t.user.screen_name as user_screen_name,
 t.user.location as user_location,
 o.csp,
 o.country,
 t.day_key
from stg_raw_twitter_userstream t
join dim_operator_view o on t.user.screen_name = o.csp_screen_name