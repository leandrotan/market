CREATE TABLE IF NOT EXISTS linked_tweets_ORC (
        createdAt String,
        lat Double,
        long Double,
        text String,
        category String,
        sentiment String,
        sectorId String,
        msisdn String
 
)
clustered by (createdAt) into 4 buckets
STORED AS ORC;
