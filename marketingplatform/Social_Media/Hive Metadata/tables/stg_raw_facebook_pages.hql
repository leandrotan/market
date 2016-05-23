CREATE EXTERNAL TABLE stg_raw_facebook_pages (
	timestamp_ms Bigint,
	page STRUCT<
		company_overview: String,
        website: String,
        name: String,
        username: String,
        description: String,
        checkins: int,
        general_info: String,
        likes: INT,
        cover: STRUCT<
                source: String,
                offset_y: INT,
                id: String,
                offset_x: INT,
                cover_id: String
        >,
        id: String,
        talking_about_count: INT,
        link: String,
        is_community_page: BOOLEAN,
		category: String,
        can_post: BOOLEAN,
        parking: STRUCT<
                valet: INT,
                street: INT,
                lot: INT
        >,
        were_here_count: INT,
        about: String,
        has_added_app: BOOLEAN,
        is_published: BOOLEAN,
        phone: String
	>,
	application STRUCT<
			namespace: String,
			name: String,
			id: String
	>,
	name String,
	source String,
	created_time String,
	description String,
	icon String,
	status_type String,
	to STRUCT<
			data: ARRAY<
					STRUCT<
							name: String,
							category_list: ARRAY<
									STRUCT<
											name: String,
											id: String
									>
							>,
							id: String,
							category: String,
							namespace: String
					>
			>
	>,
	likes STRUCT<
			data: ARRAY<
					STRUCT<
							name: String,
							id: String
					>
			>,
			paging: STRUCT<
					next: String,
					cursors: STRUCT<
							after: String,
							before: String
					>
			>
	>,
	privacy STRUCT<
			description: STRING,
			friends: STRING,
			allow: STRING,
			deny: STRING,
			value: STRING
	>,
	id String,
	properties ARRAY<
			STRUCT<
					text: String,
					name: String
			>
	>,
	shares STRUCT<
			count: INT
	>,
	caption String,
	story String,
	link String,
	actions ARRAY<
			STRUCT<
					link: String,
					name: String
			>
	>,
	picture String,
	message String,
	with_tags STRUCT<
			data: ARRAY<
					STRUCT<
							category: String,
							name: String,
							id: String
					>
			>
	>,
	place STRUCT<
			location: STRUCT<
					city: String,
					latitude: DOUBLE,
					zip: String,
					country: String,
					longitude: DOUBLE,
					located_in: String,
					street: String
			>,
			name: String,
			id: String
	>,
	updated_time String,
	type String,
	object_id String,
	fr0m STRUCT<
			category: String,
			name: String,
			id: String
	>,
	comments STRUCT<
			data: ARRAY<
					STRUCT<
							can_remove: BOOLEAN,
							created_time: String,
							like_count: INT,
							message_tags: ARRAY<
									STRUCT<
											name: String,
											id: String,
											offset: INT,
											length: INT,
											type: String
									>
							>,
							id: String,
							message: String,
							user_likes: BOOLEAN,
							fr0m: STRUCT<
									category: String,
									name: String,
									id: String
							>,
							parent: STRUCT<
								can_remove: BOOLEAN,
								created_time: String,
								like_count: INT,
								message_tags: ARRAY<
										STRUCT<
												name: String,
												id: String,
												offset: INT,
												length: INT,
												type: String
										>
								>,
								id: String,
								message: String,
								user_likes: BOOLEAN,
								fr0m: STRUCT<
										category: String,
										name: String,
										id: String
								>
							>
					>
			>,
			paging: STRUCT<
					next: String,
					cursors: STRUCT<
							after: String,
							before: String
					>
			>
	>
) PARTITIONED BY (country String, month_key String) 
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/SocialMedia/Facebook/Pages';