CREATE EXTERNAL TABLE stg_raw_facebook_pages_info (
    timestamp_ms Bigint,
    website String,
    name String,
    username String,
    description String,
    checkins int,
    location STRUCT<
        zip: String,
        street: String,
        country: String,
        city: String
    >,
    general_info String,
    likes INT,
    cover STRUCT<
            source: String,
            offset_y: INT,
            id: String,
            offset_x: INT,
            cover_id: String
    >,
    id String,
    talking_about_count INT,
    link String,
    is_community_page BOOLEAN,
    category String,
    can_post BOOLEAN,
    parking STRUCT<
            valet: INT,
            street: INT,
            lot: INT
    >,
    were_here_count INT,
    about String,
    has_added_app BOOLEAN,
    is_published BOOLEAN,
    phone String
) PARTITIONED BY (country String, month_key String) 
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/SocialMedia/Facebook/Pages_Info';