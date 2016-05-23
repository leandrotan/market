package com.wicom.kafka.streaming.twitter;

/**
 * Created by albert.elkhoury on 2/18/2015.
 */
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TwitterProducer {
    private static final Logger logger = LoggerFactory.getLogger(TwitterProducer.class);

    /** Information necessary for accessing the Twitter API */
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private String[] keywords;//filtering on keywords is not implemented
    private String[] languages;//filtering on languages is not implemented

    private double[][] locations;// ={{-122.75,36.8},{-121.75,37.8}};
    private boolean isLocationFiltered = false;
    private boolean isLanguageFiltered = false;

    /** The actual Twitter stream. It's set up to collect raw JSON data */
    private TwitterStream twitterStream;

    private void start(final Context context) {

        /** Producer properties **/
        Properties props = new Properties();
        props.put("metadata.broker.list", context.getString(TwitterSourceConstant.BROKER_LIST));
        props.put("serializer.class", context.getString(TwitterSourceConstant.SERIALIZER));
        props.put("request.required.acks", context.getString(TwitterSourceConstant.REQUIRED_ACKS));

        ProducerConfig config = new ProducerConfig(props);

        final Producer<String, String> producer = new Producer<String, String>(config);

        /** Twitter properties **/
        consumerKey = context.getString(TwitterSourceConstant.CONSUMER_KEY_KEY);
        consumerSecret = context.getString(TwitterSourceConstant.CONSUMER_SECRET_KEY);
        accessToken = context.getString(TwitterSourceConstant.ACCESS_TOKEN_KEY);
        accessTokenSecret = context.getString(TwitterSourceConstant.ACCESS_TOKEN_SECRET_KEY);
        keywords=processKeywords(context.getString(TwitterSourceConstant.KEYWORDS_KEY));
        languages=processKeywords(context.getString(TwitterSourceConstant.LANGUAGES_KEY));
        locations=processLocations(context.getString(TwitterSourceConstant.TWITTER_LOCATIONS));


        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthAccessToken(accessToken);
        cb.setOAuthAccessTokenSecret(accessTokenSecret);
        cb.setJSONStoreEnabled(true);
        cb.setIncludeEntitiesEnabled(true);

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        final Map<String, String> headers = new HashMap<String, String>();

        /** Twitter listener **/
        StatusListener listener = new StatusListener() {
            // The onStatus method is executed every time a new tweet comes in.
            public void onStatus(Status status) {
                // The EventBuilder is used to build an event using the
                // the raw JSON of a tweet
                StatusStreamJSON publicStreamJSON = new StatusStreamJSON(status);
                Map<String,String> tweetTuple= new HashMap<String, String>();

                if (keywords.length!=0) {
                    publicStreamJSON.setMatchedKeywordsTag(keywords);
                    // maybe below is not needed
                    //KeyedMessage<String, String> data = new KeyedMessage<String, String>(context.getString(TwitterSourceConstant.KAFKA_TOPIC)
                     //       , DataObjectFactory.getRawJSON(status));
                   // producer.send(data);
                }

                if (isLocationFiltered) {
                    if (publicStreamJSON.isMatched(keywords) || keywords.length==0) {
                        KeyedMessage<String, String> data = new KeyedMessage<String, String>(context.getString(TwitterSourceConstant.KAFKA_TOPIC)
                                , DataObjectFactory.getRawJSON(status));
                        producer.send(data);
                    }
                } else {
                    KeyedMessage<String, String> data = new KeyedMessage<String, String>(context.getString(TwitterSourceConstant.KAFKA_TOPIC)
                            , DataObjectFactory.getRawJSON(status));
                    producer.send(data);
                }
           }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }
            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }
            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            public void onException(Exception ex) {
                logger.info("Shutting down Twitter sample stream...");
                System.out.println("Shutting down Twitter sample stream...");
                ex.printStackTrace();
                twitterStream.shutdown();
            }

        };

        /** Bind the listener **/
        twitterStream.addListener(listener);
        /** GOGOGO **/
        if (locations.length != 0) {
            logger.info("Starting up Twitter status with location filter...");
            System.out.println("Starting up Twitter status with location filter...");
            logger.debug(ArrayUtils.toString(locations));
            twitterStream.filter(new FilterQuery().locations(locations));
            isLocationFiltered = true;
        } else if (keywords.length != 0) {
            logger.info("Starting up Twitter status filtering with keywords filter...");
            System.out.println("Starting up Twitter status filtering with keywords filter...");
            logger.debug(ArrayUtils.toString(keywords));
            twitterStream.filter(new FilterQuery().track(keywords));
        } /*else if (languages.length != 0)        {
            logger.info("Starting up Twitter status filtering with languages filter...");
            System.out.println("Starting up Twitter status filtering with languages filter...");
            logger.debug(ArrayUtils.toString(languages));
            twitterStream.filter(new FilterQuery().language(languages));
            isLanguageFiltered = true;
        }*/
        else {
            logger.info("Starting up Twitter Public status sampling...");
            System.out.println("Starting up Twitter Public status sampling...");
            twitterStream.sample();
        }
    }

    private String[] processKeywords(String keywordString) {
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

    public static void main(String[] args) {
        try {
            Context context = new Context(args[0]);
            TwitterProducer tp = new TwitterProducer();
            tp.start(context);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //System.out.println("Exception Thrown");
            logger.info(e.getMessage());
        }

    }
}
