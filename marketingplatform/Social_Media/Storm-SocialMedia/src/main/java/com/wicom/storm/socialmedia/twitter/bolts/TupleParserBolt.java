package com.wicom.storm.socialmedia.twitter.bolts;

import backtype.storm.topology.BasicOutputCollector;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

//import twitter4j.internal.org.json.JSONException;
//import twitter4j.internal.org.json.JSONObject;

import backtype.storm.tuple.Values;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Charbel Hobeika on 5/8/2015.
 */
public class TupleParserBolt extends BaseBasicBolt {
    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        String tweetJSON = (String) tuple.getValueByField("tweet");
        String COUNTRY = (String) tuple.getValueByField("country");

        try {
            JSONObject tweet = new JSONObject(tweetJSON);
/*
            if (!tweet.has("retweeted_status"))
            {
                tweet.put("retweeted_status", "null");
            }
*/
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            Long timestamp=Long.valueOf(tweet.get("timestamp_ms").toString().isEmpty()?"0":tweet.get("timestamp_ms").toString());
            String day_key=df.format(new Date(timestamp));
/*
            Values values = new Values(
                    tweet.get("coordinates"),
                    tweet.get("retweeted"),
                    tweet.get("source"),
                    //tweet.get("matched_keywords"),
                    tweet.get("entities"),
                    tweet.get("favorite_count"),
                    tweet.get("in_reply_to_status_id_str"),
                    tweet.get("geo"),
                    tweet.get("id_str"),
                    tweet.get("in_reply_to_user_id"),
                    tweet.get("timestamp_ms"),
                    tweet.get("truncated"),
                    tweet.get("text"),
                    tweet.get("retweet_count"),
                    //tweet.get("retweeted_status"),
                    tweet.get("id"),
                    tweet.get("in_reply_to_status_id"),
                    tweet.get("possibly_sensitive"),
                    tweet.get("filter_level"),
                    tweet.get("created_at"),
                    tweet.get("place"),
                    tweet.get("favorited"),
                    tweet.get("lang"),
                    tweet.get("contributors"),
                    tweet.get("in_reply_to_screen_name"),
                    tweet.get("in_reply_to_user_id_str"),
                    tweet.get("user"),
                    COUNTRY,
                    day_key);
            */
            Values values = new Values(
                    tweet.get("coordinates"),
                    tweet.get("retweeted"),
                    tweet.get("source"),
                    //tweet.get("matched_keywords"),
                    tweet.get("entities"),
                    tweet.get("favorite_count"),
                    tweet.get("in_reply_to_status_id_str"),
                    tweet.get("geo"),
                    tweet.get("id_str"),
                    tweet.get("in_reply_to_user_id"),
                    tweet.get("timestamp_ms"),
                    tweet.get("truncated"),
                    tweet.get("text"),
                    tweet.get("retweet_count"),
                    //tweet.get("retweeted_status"),
                    tweet.get("id"),
                    tweet.get("in_reply_to_status_id"),
                    tweet.get("possibly_sensitive"),
                    tweet.get("filter_level"),
                    tweet.get("created_at"),
                    tweet.get("place"),
                    tweet.get("favorited"),
                    tweet.get("lang"),
                    tweet.get("contributors"),
                    tweet.get("in_reply_to_screen_name"),
                    tweet.get("in_reply_to_user_id_str"),
                    tweet.get("user"),
                    COUNTRY,
                    day_key);
            outputCollector.emit(values);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofDeclarer) {
        ofDeclarer.declare(new Fields(
                "coordinates",
                "retweeted",
                "source",
                //"matched_keywords",
                "entities",
                "favorite_count",
                "in_reply_to_status_id_str",
                "geo",
                "id_str",
                "in_reply_to_user_id",
                "timestamp_ms",
                "truncated",
                "text",
                "retweet_count",
                //"retweeted_status",
                "id",
                "in_reply_to_status_id",
                "possibly_sensitive",
                "filter_level",
                "created_at",
                "place",
                "favorited",
                "lang",
                "contributors",
                "in_reply_to_screen_name",
                "in_reply_to_user_id_str",
                "user",
                "country",
                "day_key"));

        /*ofDeclarer.declare(new Fields(
                "coordinates",
                "retweeted",
                "source",
                //"matched_keywords",
                "entities",
                "favorite_count",
                "in_reply_to_status_id_str",
                "geo",
                "id_str",
                "in_reply_to_user_id",
                "timestamp_ms",
                "truncated",
                "text",
                "retweet_count",
                //"retweeted_status",
                "id",
                "in_reply_to_status_id",
                "possibly_sensitive",
                "filter_level",
                "created_at",
                "place",
                "favorited",
                "lang",
                "contributors",
                "in_reply_to_screen_name",
                "in_reply_to_user_id_str",
                "user",
                "country",
                "day_key"));
        */

    }
}
