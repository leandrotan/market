package com.wicom.storm.socialmedia.twitter.bolts;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.wicom.storm.socialmedia.twitter.utils.StatusStreamJSON;

import java.util.List;

/**
 * Created by Charbel Hobeika on 5/8/2015.
 */
public class PrinterBolt extends BaseBasicBolt {

    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        //StatusStreamJSON publicStreamJSON = new StatusStreamJSON(tuple);

        //System.out.println(tuple);
        String tweetJSON = (String) tuple.getValueByField("tweet");
        String COUNTRY = "country="+(String) tuple.getValueByField("country");

        /*Fields fields = tuple.getFields();
        List<String> listOfFields =fields.toList();
        for(String field:listOfFields) {
            System.out.println(field + "=" + tuple.getValueByField(field));
        }
*/

        //System.out.println(tuple);
        //Status status = (Status) tuple.getValueByField("tweet");
        //System.out.println(status);
    /*    try {
            String tweetJSON = (String) tuple.getValueByField(tuple.getStringByField("tweet")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Fields fields = tuple.getFields();
        List<String> listOfFields =fields.toList();
        for(String field:listOfFields) {
            System.out.println(field + "::" + tuple.getValueByField(field));
        }
  */
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofDeclarer) {
        ofDeclarer.declare(new Fields("coordinates","retweeted","source"));//,"matched_keywords","entities","favorite_count","in_reply_to_status_id_str","geo","id_str","in_reply_to_user_id","timestamp_ms","truncated","text","retweet_count","retweeted_status","id","in_reply_to_status_id","possibly_sensitive","filter_level","created_at","place","favorited","lang","contributors","in_reply_to_screen_name","in_reply_to_user_id_str","user","sentiment_score","entity","entity_subject","day_key"));

    }
}
