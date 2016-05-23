CREATE TABLE IF NOT EXISTS unlinked_tweets_ORC (
        createdAt String,
        lat Double,
        long Double,
        text String,
        sectorId String
 
)
clustered by (createdAt) into 4 buckets
STORED AS ORC;