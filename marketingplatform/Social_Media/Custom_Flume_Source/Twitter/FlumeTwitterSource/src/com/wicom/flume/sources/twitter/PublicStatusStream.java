package com.wicom.flume.sources.twitter;

import static org.apache.flume.interceptor.TimestampInterceptor.Constants.TIMESTAMP;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.sources.twitter.feeds.StatusStreamJSON;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class PublicStatusStream extends AbstractSource
    						   implements EventDrivenSource, Configurable {
	
	private static final Logger logger = LoggerFactory.getLogger(PublicStatusStream.class);
	
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private String selectorHeader;
	private String[] keywords;
	private double[][] locations;
  	private TwitterStream twitterStream;
  	
  	private boolean isLocationFiltered = false;

	public void configure(Context context) {
		consumerKey = context.getString("consumerKey");
	    consumerSecret = context.getString("consumerSecret");
	    accessToken = context.getString("accessToken");
	    accessTokenSecret = context.getString("accessTokenSecret");
	    selectorHeader = context.getString("selector.header");
	    keywords = processKeywords(context.getString("keywords", ""));
	    locations = processLocations(context.getString("locations", ""));
	    
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setOAuthConsumerKey(consumerKey);
	    cb.setOAuthConsumerSecret(consumerSecret);
	    cb.setOAuthAccessToken(accessToken);
	    cb.setOAuthAccessTokenSecret(accessTokenSecret);
	    cb.setJSONStoreEnabled(true);
	    cb.setIncludeEntitiesEnabled(true);
	    cb.setDebugEnabled(false);
	    twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
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

	@Override
	public void start() {
		final ChannelProcessor channel = getChannelProcessor();
		final Map<String, String> headers = new HashMap<String, String>();

		StatusListener listener = new StatusListener() {
			// The onStatus method is executed every time a new tweet comes in.
			public void onStatus(Status status) {
				headers.put(TIMESTAMP, Long.toString(status.getCreatedAt().getTime()));
				if (StatusStreamJSON.isNormal(DataObjectFactory.getRawJSON(status))) {
					headers.put(selectorHeader, "NORMAL");
				} else {
					logger.error("Add error message header");
					headers.put(selectorHeader, "ERROR");
				}
				
				StatusStreamJSON publicStreamJSON = new StatusStreamJSON(status);
				if (keywords.length!=0) {
					publicStreamJSON.setMatchedKeywordsTag(keywords);
				}
				if (isLocationFiltered) {
					if (publicStreamJSON.isMatched(keywords) || keywords.length==0) {
						Event event = EventBuilder.withBody(publicStreamJSON.toString().getBytes(), headers);
						channel.processEvent(event);
					}
				} else {
					Event event = EventBuilder.withBody(publicStreamJSON.toString().getBytes(), headers);
					channel.processEvent(event);
				}
			}
			// This listener will ignore everything except for new tweets
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
			public void onScrubGeo(long userId, long upToStatusId) {}
			public void onException(Exception ex) {}
			public void onStallWarning(StallWarning warning) {}
		};

		twitterStream.addListener(listener);

		if (locations.length != 0) {
			logger.info("Starting up Twitter Public status with location filter...");
			logger.debug(ArrayUtils.toString(locations));
			twitterStream.filter(new FilterQuery().locations(locations));
			isLocationFiltered = true;
		} else if (keywords.length != 0) {
			logger.info("Starting up Twitter Public status filtering with keywords filter...");
			logger.debug(ArrayUtils.toString(keywords));
			twitterStream.filter(new FilterQuery().track(keywords));
		} else {
			logger.info("Starting up Twitter Public status sampling...");
			twitterStream.sample();
		}
		super.start();
	}

	@Override
	public void stop() {
		logger.info("Shutting down PublicStatus sample stream...");
		twitterStream.shutdown();
		super.stop();
	}
}
