CREATE TABLE stg_raw_twitter_accounts (
	utc_offset INT,
	name STRING,
	friends_count INT,
	screen_name STRING,
	entities STRUCT<
		description: STRUCT<
			urls: STRING
		>,
		url: STRUCT<
			urls: ARRAY<
				STRUCT<
					expanded_url: STRING,
					indices: ARRAY<
						INT
					>,
					display_url: STRING,
					url: STRING
				>
			>
		>
	>,
	location STRING,
	protected BOOLEAN,
	url STRING,
	profile_image_url STRING,
	profile_background_color STRING,
	profile_use_background_image BOOLEAN,
	is_translator BOOLEAN,
	geo_enabled BOOLEAN,
	description STRING,
	profile_link_color STRING,
	id_str STRING,
	listed_count INT,
	timestamp_ms BIGINT,
	default_profile_image BOOLEAN,
	followers_count INT,
	profile_image_url_https STRING,
	profile_sidebar_border_color STRING,
	profile_background_image_url STRING,
	favourites_count INT,
	default_profile BOOLEAN,
	id INT,
	status STRUCT<
		contributors: STRING,
		text: STRING,
		geo: STRING,
		retweeted: BOOLEAN,
		in_reply_to_screen_name: STRING,
		truncated: BOOLEAN,
		lang: STRING,
		entities: STRUCT<
			symbols: STRING,
			urls: STRING,
			hashtags: STRING,
			user_mentions: ARRAY<
				STRUCT<
					id: INT,
					name: STRING,
					indices: ARRAY<
						INT
					>,
					screen_name: STRING,
					id_str: STRING
				>
			>
		>,
		in_reply_to_status_id_str: STRING,
		id: BIGINT,
		source: STRING,
		in_reply_to_user_id_str: STRING,
		favorited: BOOLEAN,
		in_reply_to_status_id: BIGINT,
		retweet_count: INT,
		created_at: STRING,
		in_reply_to_user_id: INT,
		favorite_count: INT,
		id_str: STRING,
		place: STRING,
		coordinates: STRING
	>,
	profile_background_tile BOOLEAN,
	contributors_enabled BOOLEAN,
	profile_location STRING,
	follow_request_sent BOOLEAN,
	created_at STRING,
	profile_sidebar_fill_color STRING,
	lang STRING,
	profile_text_color STRING,
	notifications BOOLEAN,
	verified BOOLEAN,
	time_zone STRING,
	profile_banner_url STRING,
	statuses_count INT,
	profile_background_image_url_https STRING,
	has_extended_profile BOOLEAN,
	is_translation_enabled BOOLEAN
)PARTITIONED BY (day_key String)  
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/SocialMedia/TwitterAccount/';
