DROP TABLE IF EXISTS twitter_publicstream_storm;
CREATE TABLE twitter_publicstream_storm (
        coordinates String,
        retweeted BOOLEAN,
        source String,
		matched_keywords ARRAY<
			String
		>,
        entities STRUCT<
                urls: ARRAY<
                        STRUCT<
                                expanded_url: String,
                                indices: ARRAY<
                                        Int
                                >,
                                display_url: String,
                                url: String
                        >
                >,
                hashtags: ARRAY<
                        STRUCT<
                                text: String,
                                indices: ARRAY<
                                        Int
                                >
                        >
                >,
                user_mentions: ARRAY<
                        STRUCT<
                                id: BIGINT,
                                name: String,
                                indices: ARRAY<
                                        Int
                                >,
                                screen_name: String,
                                id_str: String
                        >
                >
        >,
        favorite_count Int,
        in_reply_to_status_id_str String,
        geo String,
        id_str String,
        in_reply_to_user_id Bigint,
        timestamp_ms String,
        truncated BOOLEAN,
        text String,
        retweet_count Int,
        retweeted_status STRUCT<
                contributors: String,
                text: String,
                geo: String,
                retweeted: BOOLEAN,
                in_reply_to_screen_name: String,
                possibly_sensitive: BOOLEAN,
                truncated: BOOLEAN,
                lang: String,
                entities: STRUCT<
                        urls: ARRAY<
                                STRUCT<
                                        expanded_url: String,
                                        indices: ARRAY<
                                                Int
                                        >,
                                        display_url: String,
                                        url: String
                                >
                        >,
                        hashtags: ARRAY<
                                STRUCT<
                                        text: String,
                                        indices: ARRAY<
                                                Int
                                        >
                                >
                        >,
                        user_mentions: ARRAY<
                                STRUCT<
                                        id: Bigint,
                                        name: String,
                                        indices: ARRAY<
                                                Int
                                        >,
                                        screen_name: String,
                                        id_str: String
                                >
                        >
                >,
                in_reply_to_status_id_str: String,
                id: BIGINT,
                source: String,
                in_reply_to_user_id_str: String,
                favorited: BOOLEAN,
                in_reply_to_status_id: String,
                retweet_count: Int,
                created_at: String,
                in_reply_to_user_id: BIGINT,
                favorite_count: Int,
                id_str: String,
                place: String,
                user: STRUCT<
                        location: String,
                        default_profile: BOOLEAN,
                        profile_background_tile: BOOLEAN,
                        statuses_count: Int,
                        lang: String,
                        profile_link_color: String,
                        profile_banner_url: String,
                        id: BIGINT,
                        protected: BOOLEAN,
                        profile_location: String,
                        favourites_count: Int,
                        profile_text_color: String,
                        description: String,
                        verified: BOOLEAN,
                        contributors_enabled: BOOLEAN,
                        profile_sidebar_border_color: String,
                        name: String,
                        profile_background_color: String,
                        created_at: String,
                        is_translation_enabled: BOOLEAN,
                        default_profile_image: BOOLEAN,
                        followers_count: Int,
                        profile_image_url_https: String,
                        geo_enabled: BOOLEAN,
                        profile_background_image_url: String,
                        profile_background_image_url_https: String,
                        follow_request_sent: String,
                        url: String,
                        utc_offset: Int,
                        time_zone: String,
                        notifications: String,
                        profile_use_background_image: BOOLEAN,
                        friends_count: Int,
                        profile_sidebar_fill_color: String,
                        screen_name: String,
                        id_str: String,
                        profile_image_url: String,
                        listed_count: Int,
                        is_translator: BOOLEAN
                >,
                coordinates: String
        >,
        id BIGINT,
        in_reply_to_status_id String,
        possibly_sensitive BOOLEAN,
        filter_level String,
        created_at String,
        place String,
        favorited BOOLEAN,
        lang String,
        contributors String,
        in_reply_to_screen_name String,
        in_reply_to_user_id_str String,
        user STRUCT<
                location: STRING,
                default_profile: BOOLEAN,
                profile_background_tile: BOOLEAN,
                statuses_count: Int,
                lang: String,
                profile_link_color: String,
                id: BIGINT,
                protected: BOOLEAN,
                profile_location: String,
                favourites_count: Int,
                profile_text_color: String,
                description: String,
                verified: BOOLEAN,
                contributors_enabled: BOOLEAN,
                profile_sidebar_border_color: String,
                name: String,
                profile_background_color: String,
                created_at: String,
                is_translation_enabled: BOOLEAN,
                default_profile_image: BOOLEAN,
                followers_count: Int,
                profile_image_url_https: String,
                geo_enabled: BOOLEAN,
                profile_background_image_url: String,
                profile_background_image_url_https: String,
                follow_request_sent: String,
                url: String,
                utc_offset: Int,
                time_zone: String,
                notifications: String,
                profile_use_background_image: BOOLEAN,
                friends_count: Int,
                profile_sidebar_fill_color: String,
                screen_name: String,
                id_str: String,
                profile_image_url: String,
                listed_count: Int,
                is_translator: BOOLEAN
        >,
        sentiment_score int,
        entity string,
        entity_subject string
) 
PARTITIONED BY (DAY_KEY String) 
clustered by (created_at) into 4 buckets
STORED AS ORC;