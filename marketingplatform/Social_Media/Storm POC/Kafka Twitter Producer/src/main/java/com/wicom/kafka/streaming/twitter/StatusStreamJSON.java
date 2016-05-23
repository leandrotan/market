package com.wicom.kafka.streaming.twitter;

/**
 * Created by Charbel Hobeika on 3/2/2015.
 */
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.json.DataObjectFactory;

public class StatusStreamJSON extends JSONObject {
    /**
     * Class represents single response from PublicStream
     * To build PublicStreamFeed use "DataObjectFactory.getRawJSON(status)" call of status object
     */
    private static final Logger logger = LoggerFactory.getLogger(StatusStreamJSON.class);

    private Status twitterStatus;

    public StatusStreamJSON(Status twitterStatus) {
        super(JSONStringHelper.replaceNewlineChar(DataObjectFactory.getRawJSON(twitterStatus)));
        this.twitterStatus = twitterStatus;
    }

    public static boolean isNormal(String rawJSON) {
        try {
            JSONObject j = new JSONObject(rawJSON);
            if (!(j.has("id") && j.has("timestamp_ms"))) {
                logger.info("JSON validation failed");
                return false;
            }
            if (Long.toString(j.getLong("timestamp_ms")) == null || j.getString("created_at") == null) {
                logger.info("JSON validation failed. Timestamp field is null");
                return false;
            }
        } catch (JSONException e) {
            logger.info("JSON validation failed. JSON is malformed");
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
        return true;
    }

    public void setMatchedKeywordsTag (String[] keywords) {
        JSONArray matchedKeywords = new JSONArray();
        if (keywords.length != 0) {
            String twitText = twitterStatus.getText();
            for (int i=0;i<keywords.length;i++) {
                String k = keywords[i];
                if (isKeywordMatched(k,twitText)) {
                    matchedKeywords.put(k);
                }
            }
        }
        this.put("matched_keywords", matchedKeywords);
    }

    private boolean isKeywordMatched (String keyword, String text) {
        String[] keyword_parts = keyword.split(" ");
        for (int i=0;i<keyword_parts.length;i++) {
            String k = keyword_parts[i].trim();
            if (!text.toLowerCase().contains(k.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    public boolean isMatched(String[] keywords) {
        String twitText = twitterStatus.getText();
        for (int i=0;i<keywords.length;i++) {
            if (isKeywordMatched(keywords[i],twitText)) {
                return true;
            }
        }
        return false;
    }
}
