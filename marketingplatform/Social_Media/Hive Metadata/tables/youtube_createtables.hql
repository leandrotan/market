CREATE EXTERNAL TABLE IF NOT EXISTS youtube_ChannelFeed (
        items ARRAY<
                STRUCT<
                        kind: STRING,
                        etag: STRING,
                        id: STRING,
                        snippet: STRUCT<
                                title: STRING,
                                description: STRING,
                                publishedAt: STRING,
                                thumbnails: STRUCT<
                                        default: STRUCT<
                                                url: STRING
                                        >,
                                        medium: STRUCT<
                                                url: STRING
                                        >,
                                        high: STRUCT<
                                                url: STRING
                                        >
                                >,
                                localized: STRUCT<
                                        title: STRING,
                                        description: STRING
                                >
                        >,
                        contentDetails: STRUCT<
                                relatedPlaylists: STRUCT<
                                        likes: STRING,
                                        favorites: STRING,
                                        uploads: STRING,
                                        watchHistory: STRING,
                                        watchLater: STRING
                                >,
                                googlePlusUserId: STRING
                        >,
                        statistics: STRUCT<
                                viewCount: STRING,
                                commentCount: STRING,
                                subscriberCount: STRING,
                                hiddenSubscriberCount: BOOLEAN,
                                videoCount: STRING
                        >,
                        topicDetails: STRUCT<
                                topicIds: ARRAY<
                                        STRING
                                >
                        >
                >
        >,
        pageInfo STRUCT<
                totalResults: INT,
                resultsPerPage: INT
        >,
        etag STRING,
        kind STRING
) PARTITIONED BY (date STRING)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/Social_Media/Youtube/ChannelFeed/';

CREATE EXTERNAL TABLE IF NOT EXISTS youtube_ChronologicalChannelKpis (
day STRING,
views INT,
comments INT,
favoritesAdded INT,
favoritesRemoved INT,
likes INT,
dislikes INT,
shares INT,
estimatedMinutesWatched DECIMAL(30,2),
averageViewDuration DECIMAL(30,2),
averageViewPercentage DECIMAL(30,2),
annotationClickThroughRate DECIMAL(30,2),
annotationCloseRate DECIMAL(30,2),
annotationImpressions DECIMAL(30,2),
annotationClickableImpressions DECIMAL(30,2),
annotationClosableImpressions DECIMAL(30,2),
annotationClicks DECIMAL(30,2),
annotationCloses DECIMAL(30,2),
subscribersGained INT,
subscribersLost INT, 
uniques INT
)
PARTITIONED BY (date STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
LOCATION '/user/sm_user/Social_Media/Youtube/ChronologicalChannelKpis/';


CREATE EXTERNAL TABLE IF NOT EXISTS youtube_Search (
        etag STRING,
        nextPageToken STRING,
        items ARRAY<
                STRUCT<
                        snippet: STRUCT<
                                liveBroadcastContent: STRING,
                                description: STRING,
                                channelTitle: STRING,
                                title: STRING,
                                channelId: STRING,
                                publishedAt: STRING,
                                thumbnails: STRUCT<
                                        medium: STRUCT<
                                                url: STRING
                                        >,
                                        high: STRUCT<
                                                url: STRING
                                        >,
                                        default: STRUCT<
                                                url: STRING
                                        >
                                >
                        >,
                        etag: STRING,
                        kind: STRING,
                        id: STRUCT<
                                videoId: STRING,
                                kind: STRING
                        >
                >
        >,
        pageInfo STRUCT<
                totalResults: INT,
                resultsPerPage: INT
        >,
        kind STRING
) PARTITIONED BY (date STRING)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/Social_Media/Youtube/Search/';

CREATE EXTERNAL TABLE IF NOT EXISTS youtube_Subscribers (
        items ARRAY<
                STRUCT<
                        etag: STRING,
                        statistics: STRUCT<
                                hiddenSubscriberCount: BOOLEAN,
                                viewCount: STRING,
                                subscriberCount: STRING,
                                videoCount: STRING,
                                commentCount: STRING
                        >,
                        snippet: STRUCT<
                                description: STRING,
                                title: STRING,
                                localized: STRUCT<
                                        description: STRING,
                                        title: STRING
                                >,
                                publishedAt: STRING,
                                thumbnails: STRUCT<
                                        medium: STRUCT<
                                                url: STRING
                                        >,
                                        high: STRUCT<
                                                url: STRING
                                        >,
                                        default: STRUCT<
                                                url: STRING
                                        >
                                >
                        >,
                        id: STRING,
                        contentDetails: STRUCT<
                                googlePlusUserId: STRING,
                                relatedPlaylists: STRUCT<
                                        likes: STRING,
                                        favorites: STRING,
                                        uploads: STRING
                                >
                        >,
                        kind: STRING
                >
        >,
        pageInfo STRUCT<
                totalResults: INT,
                resultsPerPage: INT
        >,
        etag STRING,
        kind STRING
) PARTITIONED BY (date STRING)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/Social_Media/Youtube/Subscribers/';

CREATE EXTERNAL TABLE IF NOT EXISTS youtube_Subscriptions (
        items ARRAY<
                STRUCT<
                        etag: STRING,
                        snippet: STRUCT<
                                resourceId: STRUCT<
                                        kind: STRING,
                                        channelId: STRING
                                >,
                                description: STRING,
                                title: STRING,
                                channelId: STRING,
                                publishedAt: STRING,
                                thumbnails: STRUCT<
                                        high: STRUCT<
                                                url: STRING
                                        >,
                                        default: STRUCT<
                                                url: STRING
                                        >
                                >
                        >,
                        id: STRING,
                        subscriberSnippet: STRUCT<
                                thumbnails: STRUCT<
                                        high: STRUCT<
                                                url: STRING
                                        >,
                                        default: STRUCT<
                                                url: STRING
                                        >
                                >,
                                description: STRING,
                                title: STRING,
                                channelId: STRING
                        >,
                        contentDetails: STRUCT<
                                totalItemCount: SMALLINT,
                                newItemCount: INT,
                                activityType: STRING
                        >,
                        kind: STRING
                >
        >,
        pageInfo STRUCT<
                totalResults: INT,
                resultsPerPage: INT
        >,
        etag STRING,
        kind STRING
) PARTITIONED BY (date STRING)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/Social_Media/Youtube/Subscriptions/';

CREATE EXTERNAL TABLE IF NOT EXISTS youtube_TopVideos (
video STRING,
views INT,
comments INT,
favoritesAdded INT,
favoritesRemoved INT,
likes INT,
dislikes INT,
shares INT,
estimatedMinutesWatched DECIMAL(30,2),
averageViewDuration DECIMAL(30,2),
averageViewPercentage DECIMAL(30,2),
subscribersGained INT,
subscribersLost INT
)
PARTITIONED BY (date STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
LOCATION '/user/sm_user/Social_Media/Youtube/TopVideos/';

CREATE EXTERNAL TABLE IF NOT EXISTS youtube_VideoComments (
        feed STRUCT<
                xmlns: STRING,
                xmlnshdpopenSearch: STRING,
                xmlnshdpyt: STRING,
                id: STRUCT<
                        hdpt: STRING
                >,
                updated: STRUCT<
                        hdpt: STRING
                >,
                category: ARRAY<
                        STRUCT<
                                scheme: STRING,
                                term: STRING
                        >
                >,
                logo: STRUCT<
                        hdpt: STRING
                >,
                link: ARRAY<
                        STRUCT<
                                rel: STRING,
                                type: STRING,
                                href: STRING
                        >
                >,
                author: ARRAY<
                        STRUCT<
                                name: STRUCT<
                                        hdpt: STRING
                                >,
                                uri: STRUCT<
                                        hdpt: STRING
                                >
                        >
                >,
                generator: STRUCT<
                        hdpt: STRING,
                        version: STRING,
                        uri: STRING
                >,
                openSearchhdptotalResults: STRUCT<
                        hdpt: INT
                >,
                openSearchhdpitemsPerPage: STRUCT<
                        hdpt: INT
                >,
                entry: ARRAY<
                        STRUCT<
                                id: STRUCT<
                                        hdpt: STRING
                                >,
                                published: STRUCT<
                                        hdpt: STRING
                                >,
                                updated: STRUCT<
                                        hdpt: STRING
                                >,
                                category: ARRAY<
                                        STRUCT<
                                                scheme: STRING,
                                                term: STRING
                                        >
                                >,
                                title: STRUCT<
                                        hdpt: STRING,
                                        type: STRING
                                >,
                                content: STRUCT<
                                        hdpt: STRING,
                                        type: STRING
                                >,
                                link: ARRAY<
                                        STRUCT<
                                                rel: STRING,
                                                type: STRING,
                                                href: STRING
                                        >
                                >,
                                author: ARRAY<
                                        STRUCT<
                                                name: STRUCT<
                                                        hdpt: STRING
                                                >,
                                                uri: STRUCT<
                                                        hdpt: STRING
                                                >
                                        >
                                >,
                                ythdpchannelId: STRUCT<
                                        hdpt: STRING
                                >,
                                ythdpgooglePlusUserId: STRUCT<
                                        hdpt: STRING
                                >,
                                ythdpreplyCount: STRUCT<
                                        hdpt: INT
                                >,
                                ythdpvideoid: STRUCT<
                                        hdpt: STRING
                                >
                        >
                >
        >,
        encoding STRING,
        version STRING
) PARTITIONED BY (date STRING)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/Social_Media/Youtube/VideoComments/';

CREATE EXTERNAL TABLE IF NOT EXISTS youtube_VideoList (
        items ARRAY<
                STRUCT<
                        etag: STRING,
                        snippet: STRUCT<
                                resourceId: STRUCT<
                                        videoId: STRING,
                                        kind: STRING
                                >,
                                description: STRING,
                                channelTitle: STRING,
                                position: INT,
                                playlistId: STRING,
                                title: STRING,
                                channelId: STRING,
                                publishedAt: STRING,
                                thumbnails: STRUCT<
                                        medium: STRUCT<
                                                url: STRING,
                                                height: INT,
                                                width: INT
                                        >,
                                        standard: STRUCT<
                                                url: STRING,
                                                height: INT,
                                                width: INT
                                        >,
                                        high: STRUCT<
                                                url: STRING,
                                                height: INT,
                                                width: INT
                                        >,
                                        default: STRUCT<
                                                url: STRING,
                                                height: INT,
                                                width: INT
                                        >
                                >
                        >,
                        id: STRING,
                        status: STRUCT<
                                privacyStatus: STRING
                        >,
                        contentDetails: STRUCT<
                                videoId: STRING
                        >,
                        kind: STRING
                >
        >,
        pageInfo STRUCT<
                totalResults: INT,
                resultsPerPage: INT
        >,
        etag STRING,
        kind STRING
) PARTITIONED BY (date STRING)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/Social_Media/Youtube/VideoList/';

CREATE external TABLE IF NOT EXISTS youtube_VideoStatistics (
        items ARRAY<
                STRUCT<
                        etag: STRING,
                        statistics: STRUCT<
                                viewCount: STRING,
                                commentCount: STRING,
                                dislikeCount: STRING,
                                favoriteCount: STRING,
                                likeCount: STRING
                        >,
                        snippet: STRUCT<
                                liveBroadcastContent: STRING,
                                categoryId: STRING,
                                description: STRING,
                                channelTitle: STRING,
                                title: STRING,
                                channelId: STRING,
                                publishedAt: STRING,
                                thumbnails: STRUCT<
                                        medium: STRUCT<
                                                url: STRING,
                                                height: SMALLINT,
                                                width: SMALLINT
                                        >,
                                        standard: STRUCT<
                                                url: STRING,
                                                height: SMALLINT,
                                                width: SMALLINT
                                        >,
                                        high: STRUCT<
                                                url: STRING,
                                                height: SMALLINT,
                                                width: SMALLINT
                                        >,
                                        default: STRUCT<
                                                url: STRING,
                                                height: INT,
                                                width: INT
                                        >
                                >
                        >,
                        topicDetails: STRUCT<
                                relevantTopicIds: ARRAY<
                                        STRING
                                >,
                                topicIds: ARRAY<
                                        STRING
                                >
                        >,
                        suggestions: STRUCT<
                                processingHints: ARRAY<
                                        STRING
                                >
                        >,
                        id: STRING,
                        player: STRUCT<
                                embedHtml: STRING
                        >,
                        fileDetails: STRUCT<
                                fileSize: STRING,
                                fileType: STRING,
                                container: STRING,
                                videoStreams: ARRAY<
                                        STRUCT<
                                                widthPixels: SMALLINT,
                                                heightPixels: SMALLINT,
                                                frameRateFps: DECIMAL(17, 15),
                                                aspectRatio: DECIMAL(17, 16),
                                                codec: STRING,
                                                bitrateBps: STRING
                                        >
                                >,
                                audioStreams: ARRAY<
                                        STRUCT<
                                                channelCount: INT,
                                                codec: STRING,
                                                bitrateBps: STRING
                                        >
                                >,
                                bitrateBps: STRING
                        >,
                        status: STRUCT<
                                embeddable: BOOLEAN,
                                privacyStatus: STRING,
                                license: STRING,
                                publicStatsViewable: BOOLEAN,
                                uploadStatus: STRING
                        >,
                        contentDetails: STRUCT<
                                duration: STRING,
                                licensedContent: BOOLEAN,
                                caption: STRING,
                                definition: STRING,
                                dimension: STRING
                        >,
                        kind: STRING,
                        processingDetails: STRUCT<
                                tagSuggestionsAvailability: STRING,
                                thumbnailsAvailability: STRING,
                                processingStatus: STRING,
                                fileDetailsAvailability: STRING,
                                editorSuggestionsAvailability: STRING,
                                processingIssuesAvailability: STRING
                        >
                >
        >,
        pageInfo STRUCT<
                totalResults: INT,
                resultsPerPage: INT
        >,
        etag STRING,
        kind STRING
)  PARTITIONED BY (date STRING)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION '/user/sm_user/Social_Media/Youtube/VideoStatistics/';

-- ignore malformed jsons for all created tables
ALTER TABLE youtube_ChannelFeed SET SERDEPROPERTIES ( "ignore.malformed.json" = "true");
ALTER TABLE youtube_Search SET SERDEPROPERTIES ( "ignore.malformed.json" = "true");
ALTER TABLE youtube_Subscribers SET SERDEPROPERTIES ( "ignore.malformed.json" = "true");
ALTER TABLE youtube_Subscriptions SET SERDEPROPERTIES ( "ignore.malformed.json" = "true");
ALTER TABLE youtube_VideoComments SET SERDEPROPERTIES ( "ignore.malformed.json" = "true");
ALTER TABLE youtube_VideoList SET SERDEPROPERTIES ( "ignore.malformed.json" = "true");
ALTER TABLE youtube_VideoStatistics SET SERDEPROPERTIES ( "ignore.malformed.json" = "true");

-- add a partition also fro all tables
ALTER TABLE youtube_ChannelFeed ADD IF NOT EXISTS PARTITION(date='2014-12-29');
ALTER TABLE youtube_ChronologicalChannelKpis ADD IF NOT EXISTS  PARTITION(date='2014-12-29');
ALTER TABLE youtube_Search ADD IF NOT EXISTS  PARTITION(date='2014-12-29');
ALTER TABLE youtube_Subscribers ADD IF NOT EXISTS  PARTITION(date='2014-12-29');
ALTER TABLE youtube_Subscriptions ADD IF NOT EXISTS  PARTITION(date='2014-12-29');
ALTER TABLE youtube_TopVideos ADD IF NOT EXISTS  PARTITION(date='2014-12-29');
ALTER TABLE youtube_VideoComments ADD IF NOT EXISTS  PARTITION(date='2014-12-29');
ALTER TABLE youtube_VideoList ADD IF NOT EXISTS  PARTITION(date='2014-12-29');
ALTER TABLE youtube_VideoStatistics ADD IF NOT EXISTS  PARTITION(date='2014-12-29');