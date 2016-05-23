CREATE EXTERNAL TABLE stg_raw_twitter_userstream (
        coordinates String,
        retweeted BOOLEAN,
        source String,
        entities STRUCT<
                trends: string,
                symbols: string,
                urls: ARRAY<
                        STRUCT<
                                expanded_url: string,
                                indices: ARRAY<
                                        INT
                                >,
                                display_url: string,
                                url: string
                        >
                >,
                hashtags: ARRAY<
                        STRUCT<
                                text: string,
                                indices: ARRAY<
                                        INT
                                >
                        >
                >,
                user_mentions: ARRAY<
                        STRUCT<
                                id: BIGINT,
                                name: string,
                                indices: ARRAY<
                                        INT
                                >,
                                screen_name: string,
                                id_str: string
                        >
                >
        >,
        favorite_count INT,
        in_reply_to_status_id_str string,
        geo STRUCT<type:string,coordinates:string>,
        id_str string,
        in_reply_to_user_id BIGINT,
        timestamp_ms string,
        truncated BOOLEAN,
        text string,
        retweet_count INT,
        matched_keywords ARRAY<
                String
        >,
        id BIGINT,
        in_reply_to_status_id BIGINT,
        possibly_sensitive BOOLEAN,
        filter_level string,
        created_at string,
        place STRUCT<
                id: string,
                place_type: string,
                bounding_box: STRUCT<
                        type: string,
                        coordinates: ARRAY<
                                ARRAY<
                                        ARRAY<
                                                DOUBLE
                                        >
                                >
                        >
                >,
                name: string,
                attributes: string,
                country_code: string,
                url: string,
                full_name: string,
                country: string
        >,
        favorited BOOLEAN,
        lang string,
        contributors string,
        in_reply_to_screen_name string,
        in_reply_to_user_id_str string,
        user STRUCT<
                location: string,
                default_profile: BOOLEAN,
                statuses_count: INT,
                profile_background_tile: BOOLEAN,
                lang: string,
                profile_link_color: string,
                profile_banner_url: string,
                id: BIGINT,
                favourites_count: INT,
                protected: BOOLEAN,
                profile_text_color: string,
                verified: BOOLEAN,
                description: string,
                contributors_enabled: BOOLEAN,
                profile_sidebar_border_color: string,
                name: string,
                profile_background_color: string,
                created_at: string,
                default_profile_image: BOOLEAN,
                followers_count: INT,
                profile_image_url_https: string,
                geo_enabled: BOOLEAN,
                profile_background_image_url: string,
                profile_background_image_url_https: string,
                follow_request_sent: BOOLEAN,
                url: string,
                utc_offset: int,
                time_zone: string,
                notifications: string,
                friends_count: INT,
                profile_use_background_image: BOOLEAN,
                profile_sidebar_fill_color: string,
                screen_name: string,
                id_str: string,
                profile_image_url: string,
                is_translator: BOOLEAN,
                listed_count: INT
        >
)
PARTITIONED BY (user_account String, day_key String) 
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/SocialMedia/Twitter_UserStream/';
