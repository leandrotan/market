package com.wicom.storm.socialmedia.twitter.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.wicom.storm.socialmedia.twitter.utils.GenericConstants;
import com.wicom.storm.socialmedia.twitter.utils.JSONStringHelper;
import com.wicom.storm.socialmedia.twitter.utils.StatusStreamJSON;
import org.apache.commons.lang.ArrayUtils;
import org.json.*;
import org.json.JSONException;
import twitter4j.*;
import twitter4j.JSONArray;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.util.concurrent.LinkedBlockingQueue;

import java.util.Map;

/**
 * Created by Charbel Hobeika on 5/8/2015.
 */
public class TwitterSpout extends BaseRichSpout {

    public static final String MESSAGE = "tweet";
     private String COUNTRY;
    private String _accessTokenSecret;
    private String _accessToken;
    private String _consumerSecret;
    private String _consumerKey;
    private String[] _keywordsFilter;
    private String[] _languagesFilter;
    private double[][] _locationsFilter;

    private SpoutOutputCollector _collector;
    private TwitterStream _twitterStream;
    private LinkedBlockingQueue _msgs;
    private FilterQuery _tweetFilterQuery;
    private Status _myStatus;


    //Constructor
   public TwitterSpout() {

    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        // setting Twitter4j OAUth
        this._consumerKey = (String) map.get(GenericConstants.TWITTER4J_OAUTH_CONSUMERKEY);
        this._consumerSecret = (String) map.get(GenericConstants.TWITTER4J_OAUTH_CONSUMERSECRET);
        this._accessToken = (String) map.get(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKEN);
        this._accessTokenSecret = (String) map.get(GenericConstants.TWITTER4J_OAUTH_ACCESSTOKENSECRET);

        this._keywordsFilter = processKeywordsLanguages((String) map.get(GenericConstants.TWITTER_FILTER_KEYWORDS));
        this._locationsFilter = processLocations((String) map.get(GenericConstants.TWITTER_FILTER_LOCATIONS));
        this._languagesFilter = processKeywordsLanguages((String) map.get(GenericConstants.TWITTER_FILTER_LANGUAGES));
         this.COUNTRY = (String) map.get(GenericConstants.TWITTER_COUNTRY);
        this._tweetFilterQuery = new FilterQuery();

        if (_consumerKey == null ||
                _consumerSecret == null ||
                _accessToken == null ||
                _accessTokenSecret == null) {
            throw new RuntimeException("Twitter4j OAuth field cannot be null");
        }

        _msgs = new LinkedBlockingQueue();
        _collector = spoutOutputCollector;

        ConfigurationBuilder _configurationBuilder = new ConfigurationBuilder();
        _configurationBuilder.setOAuthConsumerKey(_consumerKey)
                .setOAuthConsumerSecret(_consumerSecret)
                .setOAuthAccessToken(_accessToken)
                .setOAuthAccessTokenSecret(_accessTokenSecret)
                .setJSONStoreEnabled(true)
        ;

        _twitterStream = new TwitterStreamFactory(_configurationBuilder.build()).getInstance();

        _twitterStream.addListener(new StatusListener() {

            @Override
            public void onStatus(Status status) {

                StatusStreamJSON publicStreamJSON = new StatusStreamJSON(status);
                    if (_keywordsFilter.length != 0) {
                        publicStreamJSON.setMatchedKeywordsTag(_keywordsFilter);
                        _msgs.offer(publicStreamJSON.toString());
                    } else {
                        _msgs.offer(JSONStringHelper.replaceNewlineChar(TwitterObjectFactory.getRawJSON(status)));
                    }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }


            @Override
            public void onException(Exception ex) {
                System.out.println("Shutting down Twitter sample stream...");
                ex.printStackTrace();
                _twitterStream.shutdown();
            }
        }
        );

        System.out.println("Filter by Keywords:"+ ArrayUtils.toString(_keywordsFilter) +" .. Languages:"+ ArrayUtils.toString(_languagesFilter));

        if (_keywordsFilter.length == 0 && _languagesFilter.length == 0)
        {
            System.out.println("No filtering");
            _twitterStream.sample();
        }

        else if (_keywordsFilter.length != 0 && _languagesFilter.length != 0) {
            System.out.println("filtering on both _keywordsFilter and _languagesFilter");

            _tweetFilterQuery.track(_keywordsFilter).language(_languagesFilter);
            _twitterStream.filter(_tweetFilterQuery);
        } else if (_keywordsFilter.length != 0 && _languagesFilter.length == 0) {
            System.out.println("_keywordsFilter but NO _languagesFilter ");

            _tweetFilterQuery.track(_keywordsFilter);
            _twitterStream.filter(_tweetFilterQuery);
        }else if (_keywordsFilter.length == 0 && _languagesFilter.length != 0) {
            System.out.println("NO _keywordsFilter but _languagesFilter ");

            _tweetFilterQuery.language(_languagesFilter);
            _twitterStream.filter(_tweetFilterQuery);
        }



        // FILTERING by keywords and Language
/* System.out.println("Filter by Keywords:"+ ArrayUtils.toString(_keywordsFilter) +" .. Locations:"+ ArrayUtils.toString(_locationsFilter));
        if (_keywordsFilter.length == 0 && _locationsFilter.length == 0)
        {
            System.out.println("No filtering");
            _twitterStream.sample();
        }

        else if (_keywordsFilter.length != 0 && _locationsFilter.length != 0) {
            System.out.println("filtering on both _keywordsFilter and _locationsFilter");
            _tweetFilterQuery.track(_keywordsFilter).locations(_locationsFilter);
            _twitterStream.filter(_tweetFilterQuery);
        } else if (_keywordsFilter.length != 0 && _locationsFilter.length == 0) {
            System.out.println("_keywordsFilter but NO _locationsFilter ");
            _tweetFilterQuery.track(_keywordsFilter);
            _twitterStream.filter(_tweetFilterQuery);
        }else if (_keywordsFilter.length == 0 && _locationsFilter.length != 0) {
            System.out.println("NO _keywordsFilter but _locationsFilter ");
            _tweetFilterQuery.locations(_locationsFilter);
            _twitterStream.filter(_tweetFilterQuery);
        }
*/
    }

    private boolean meetsConditions(Status status) {
        return true;
    }

    /**
     * When requested for next tuple, reads message from queue and emits the message.
     */
    @Override
    public void nextTuple() {
        // emit tweets
        Object s = _msgs.poll();
        if (s == null) {
            //System.out.println("Empty queue");
            Utils.sleep(1000);
        } else {
            _collector.emit(new Values(s,this.COUNTRY));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(MESSAGE, "country"));
    }
    @Override
    public void close() {
        _twitterStream.shutdown();
        super.close();
    }

    @Override
    public void ack(Object msgId){
        _msgs.remove(msgId);
    }

    private String[] processKeywordsLanguages(String keywordString) {
        String[] ret;
        if (keywordString.trim().length() == 0) {
            return new String[0];
        } else {
            ret = keywordString.split(",");
            for (int i = 0; i < ret.length; i++) {
                ret[i] = ret[i].trim();
            }
            return ret;
        }
    }

    private double[][] processLocations(String locationsString) {
        double[][] list;
        if (locationsString.trim().length() == 0) {
            return new double[0][0];
        } else {
            list = new double[locationsString.split(",").length/2][2];
            int y=0;
            for (int i=0; i < locationsString.split(",").length; i=i+2) {
                double[] pair = new double[2];
                pair[0]=Double.parseDouble(locationsString.split(",")[i]);
                pair[1]=Double.parseDouble(locationsString.split(",")[i+1]);
                list[y]=pair;
                y++;
            }
        }
        return list;
    }
/*
    public void setMatchedKeywordsTag (String[] keywords, Status _status) throws JSONException {
        org.json.JSONArray matchedKeywords = new org.json.JSONArray();
        if (keywords.length != 0) {
            String twitText = _status.getText();
            for (int i=0;i<keywords.length;i++) {
                String k = keywords[i];
                if (isKeywordMatched(k,twitText)) {
                    matchedKeywords.put(k);
                }
            }
        }
        _status.put("matched_keywords", matchedKeywords);
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
*/
}