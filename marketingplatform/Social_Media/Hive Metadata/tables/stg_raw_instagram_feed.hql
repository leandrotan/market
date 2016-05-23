CREATE EXTERNAL TABLE stg_raw_instagram_feed (
    timestamp_ms Bigint,
    tags ARRAY <String>,
    location String,
    link String,
    user_has_liked Boolean,
    caption STRUCT<
        id: String,
        text: String,
        created_time: INT,
        fr0m: STRUCT<
            id: String,
            username: String,
            profile_picture: String,
            full_name: String
        >
    >,
    type String,
    id String,
    likes STRUCT<
        count: INT,
        data: ARRAY<
            STRUCT<
                id: String,
                profile_picture: String,
                username: String,
                full_name: String
            >
        >
    >,
    images STRUCT<
        thumbnail: STRUCT<
            height: INT,
            width: INT,
            url: String
        >,
        low_resolution: STRUCT<
            height: INT,
            width: INT,
            url: String
        >,
        standard_resolution: STRUCT<
            height: INT,
            width: INT,
            url: String
        >
    >,
    created_time BIGINT,
    users_in_photo String,
    user STRUCT<
        id: String,
        profile_picture: String,
        username: String,
        full_name: String
    >,
    filter String,
    comments STRUCT<
        count: INT,
        data: ARRAY<
            STRUCT<
                id: String,
                text: String,
                created_time: BIGINT,
                fr0m: STRUCT<
                    id: String,
                    username: String,
                    profile_picture: String,
                    full_name: String
                >
            >
        >
    >,
    attribution String
) PARTITIONED BY (month_key String) 
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/SocialMedia/Instagram/Feeds';