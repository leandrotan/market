create table fact_orc_raw_twitter_userstream (
 id string,
 in_reply_to_status_id string,
 coordinates string,
 source string,
 text string,
 created_at string,
 timestamp_ms string,
 user_id string,
 user_name string,
 user_screen_name string,
 user_location string,
 csp string
)
partitioned by (country String, day_key String) stored as orc;