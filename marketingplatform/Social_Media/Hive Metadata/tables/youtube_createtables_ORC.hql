create  table if not exists youtube_channelfeed_orc (
        items array<
                struct<
                        kind: string,
                        etag: string,
                        id: string,
                        snippet: struct<
                                title: string,
                                description: string,
                                publishedat: string,
                                thumbnails: struct<
                                        default: struct<
                                                url: string
                                        >,
                                        medium: struct<
                                                url: string
                                        >,
                                        high: struct<
                                                url: string
                                        >
                                >,
                                localized: struct<
                                        title: string,
                                        description: string
                                >
                        >,
                        contentdetails: struct<
                                relatedplaylists: struct<
                                        likes: string,
                                        favorites: string,
                                        uploads: string,
                                        watchhistory: string,
                                        watchlater: string
                                >,
                                googleplususerid: string
                        >,
                        statistics: struct<
                                viewcount: string,
                                commentcount: string,
                                subscribercount: string,
                                hiddensubscribercount: boolean,
                                videocount: string
                        >,
                        topicdetails: struct<
                                topicids: array<
                                        string
                                >
                        >
                >
        >,
        pageinfo struct<
                totalresults: int,
                resultsperpage: int
        >,
        etag string,
        kind string
) partitioned by (date string)
clustered by (etag) into 5 buckets
stored as orc;

create  table if not exists youtube_chronologicalchannelkpis_orc (
day string,
views int,
comments int,
favoritesadded int,
favoritesremoved int,
likes int,
dislikes int,
shares int,
estimatedminuteswatched decimal(30,2),
averageviewduration decimal(30,2),
averageviewpercentage decimal(30,2),
annotationclickthroughrate decimal(30,2),
annotationcloserate decimal(30,2),
annotationimpressions decimal(30,2),
annotationclickableimpressions decimal(30,2),
annotationclosableimpressions decimal(30,2),
annotationclicks decimal(30,2),
annotationcloses decimal(30,2),
subscribersgained int,
subscriberslost int, 
uniques int
)
partitioned by (date string)
clustered by (day) into 5 buckets
stored as orc;


create table if not exists youtube_search_orc (
        etag string,
        nextpagetoken string,
        items array<
                struct<
                        snippet: struct<
                                livebroadcastcontent: string,
                                description: string,
                                channeltitle: string,
                                title: string,
                                channelid: string,
                                publishedat: string,
                                thumbnails: struct<
                                        medium: struct<
                                                url: string
                                        >,
                                        high: struct<
                                                url: string
                                        >,
                                        default: struct<
                                                url: string
                                        >
                                >
                        >,
                        etag: string,
                        kind: string,
                        id: struct<
                                videoid: string,
                                kind: string
                        >
                >
        >,
        pageinfo struct<
                totalresults: int,
                resultsperpage: int
        >,
        kind string
) partitioned by (date string)
clustered by (etag) into 5 buckets
stored as orc;

create table if not exists youtube_subscribers_orc (
        items array<
                struct<
                        etag: string,
                        statistics: struct<
                                hiddensubscribercount: boolean,
                                viewcount: string,
                                subscribercount: string,
                                videocount: string,
                                commentcount: string
                        >,
                        snippet: struct<
                                description: string,
                                title: string,
                                localized: struct<
                                        description: string,
                                        title: string
                                >,
                                publishedat: string,
                                thumbnails: struct<
                                        medium: struct<
                                                url: string
                                        >,
                                        high: struct<
                                                url: string
                                        >,
                                        default: struct<
                                                url: string
                                        >
                                >
                        >,
                        id: string,
                        contentdetails: struct<
                                googleplususerid: string,
                                relatedplaylists: struct<
                                        likes: string,
                                        favorites: string,
                                        uploads: string
                                >
                        >,
                        kind: string
                >
        >,
        pageinfo struct<
                totalresults: int,
                resultsperpage: int
        >,
        etag string,
        kind string
) partitioned by (date string)
clustered by (etag) into 5 buckets
stored as orc;

create table if not exists youtube_subscriptions_orc (
        items array<
                struct<
                        etag: string,
                        snippet: struct<
                                resourceid: struct<
                                        kind: string,
                                        channelid: string
                                >,
                                description: string,
                                title: string,
                                channelid: string,
                                publishedat: string,
                                thumbnails: struct<
                                        high: struct<
                                                url: string
                                        >,
                                        default: struct<
                                                url: string
                                        >
                                >
                        >,
                        id: string,
                        subscribersnippet: struct<
                                thumbnails: struct<
                                        high: struct<
                                                url: string
                                        >,
                                        default: struct<
                                                url: string
                                        >
                                >,
                                description: string,
                                title: string,
                                channelid: string
                        >,
                        contentdetails: struct<
                                totalitemcount: smallint,
                                newitemcount: int,
                                activitytype: string
                        >,
                        kind: string
                >
        >,
        pageinfo struct<
                totalresults: int,
                resultsperpage: int
        >,
        etag string,
        kind string
) partitioned by (date string)
clustered by (etag) into 5 buckets
stored as orc;

create  table if not exists youtube_topvideos_orc (
video string,
views int,
comments int,
favoritesadded int,
favoritesremoved int,
likes int,
dislikes int,
shares int,
estimatedminuteswatched decimal(30,2),
averageviewduration decimal(30,2),
averageviewpercentage decimal(30,2),
subscribersgained int,
subscriberslost int
)
partitioned by (date string)
clustered by (video) into 5 buckets
stored as orc;

create table if not exists youtube_videocomments_orc (
        feed struct<
                xmlns: string,
                xmlnshdpopensearch: string,
                xmlnshdpyt: string,
                id: struct<
                        hdpt: string
                >,
                updated: struct<
                        hdpt: string
                >,
                category: array<
                        struct<
                                scheme: string,
                                term: string
                        >
                >,
                logo: struct<
                        hdpt: string
                >,
                link: array<
                        struct<
                                rel: string,
                                type: string,
                                href: string
                        >
                >,
                author: array<
                        struct<
                                name: struct<
                                        hdpt: string
                                >,
                                uri: struct<
                                        hdpt: string
                                >
                        >
                >,
                generator: struct<
                        hdpt: string,
                        version: string,
                        uri: string
                >,
                opensearchhdptotalresults: struct<
                        hdpt: int
                >,
                opensearchhdpitemsperpage: struct<
                        hdpt: int
                >,
                entry: array<
                        struct<
                                id: struct<
                                        hdpt: string
                                >,
                                published: struct<
                                        hdpt: string
                                >,
                                updated: struct<
                                        hdpt: string
                                >,
                                category: array<
                                        struct<
                                                scheme: string,
                                                term: string
                                        >
                                >,
                                title: struct<
                                        hdpt: string,
                                        type: string
                                >,
                                content: struct<
                                        hdpt: string,
                                        type: string
                                >,
                                link: array<
                                        struct<
                                                rel: string,
                                                type: string,
                                                href: string
                                        >
                                >,
                                author: array<
                                        struct<
                                                name: struct<
                                                        hdpt: string
                                                >,
                                                uri: struct<
                                                        hdpt: string
                                                >
                                        >
                                >,
                                ythdpchannelid: struct<
                                        hdpt: string
                                >,
                                ythdpgoogleplususerid: struct<
                                        hdpt: string
                                >,
                                ythdpreplycount: struct<
                                        hdpt: int
                                >,
                                ythdpvideoid: struct<
                                        hdpt: string
                                >
                        >
                >
        >,
        encoding string,
        version string
) partitioned by (date string)
clustered by (encoding) into 5 buckets
stored as orc;

create table if not exists youtube_videolist_orc (
        items array<
                struct<
                        etag: string,
                        snippet: struct<
                                resourceid: struct<
                                        videoid: string,
                                        kind: string
                                >,
                                description: string,
                                channeltitle: string,
                                position: int,
                                playlistid: string,
                                title: string,
                                channelid: string,
                                publishedat: string,
                                thumbnails: struct<
                                        medium: struct<
                                                url: string,
                                                height: int,
                                                width: int
                                        >,
                                        standard: struct<
                                                url: string,
                                                height: int,
                                                width: int
                                        >,
                                        high: struct<
                                                url: string,
                                                height: int,
                                                width: int
                                        >,
                                        default: struct<
                                                url: string,
                                                height: int,
                                                width: int
                                        >
                                >
                        >,
                        id: string,
                        status: struct<
                                privacystatus: string
                        >,
                        contentdetails: struct<
                                videoid: string
                        >,
                        kind: string
                >
        >,
        pageinfo struct<
                totalresults: int,
                resultsperpage: int
        >,
        etag string,
        kind string
) partitioned by (date string)
clustered by (etag) into 5 buckets
stored as orc;

create table if not exists youtube_videostatistics_orc (
        items array<
                struct<
                        etag: string,
                        statistics: struct<
                                viewcount: string,
                                commentcount: string,
                                dislikecount: string,
                                favoritecount: string,
                                likecount: string
                        >,
                        snippet: struct<
                                livebroadcastcontent: string,
                                categoryid: string,
                                description: string,
                                channeltitle: string,
                                title: string,
                                channelid: string,
                                publishedat: string,
                                thumbnails: struct<
                                        medium: struct<
                                                url: string,
                                                height: smallint,
                                                width: smallint
                                        >,
                                        standard: struct<
                                                url: string,
                                                height: smallint,
                                                width: smallint
                                        >,
                                        high: struct<
                                                url: string,
                                                height: smallint,
                                                width: smallint
                                        >,
                                        default: struct<
                                                url: string,
                                                height: int,
                                                width: int
                                        >
                                >
                        >,
                        topicdetails: struct<
                                relevanttopicids: array<
                                        string
                                >,
                                topicids: array<
                                        string
                                >
                        >,
                        suggestions: struct<
                                processinghints: array<
                                        string
                                >
                        >,
                        id: string,
                        player: struct<
                                embedhtml: string
                        >,
                        filedetails: struct<
                                filesize: string,
                                filetype: string,
                                container: string,
                                videostreams: array<
                                        struct<
                                                widthpixels: smallint,
                                                heightpixels: smallint,
                                                frameratefps: decimal(17, 15),
                                                aspectratio: decimal(17, 16),
                                                codec: string,
                                                bitratebps: string
                                        >
                                >,
                                audiostreams: array<
                                        struct<
                                                channelcount: int,
                                                codec: string,
                                                bitratebps: string
                                        >
                                >,
                                bitratebps: string
                        >,
                        status: struct<
                                embeddable: boolean,
                                privacystatus: string,
                                license: string,
                                publicstatsviewable: boolean,
                                uploadstatus: string
                        >,
                        contentdetails: struct<
                                duration: string,
                                licensedcontent: boolean,
                                caption: string,
                                definition: string,
                                dimension: string
                        >,
                        kind: string,
                        processingdetails: struct<
                                tagsuggestionsavailability: string,
                                thumbnailsavailability: string,
                                processingstatus: string,
                                filedetailsavailability: string,
                                editorsuggestionsavailability: string,
                                processingissuesavailability: string
                        >
                >
        >,
        pageinfo struct<
                totalresults: int,
                resultsperpage: int
        >,
        etag string,
        kind string
)  partitioned by (date string)
clustered by (etag) into 5 buckets
stored as orc;