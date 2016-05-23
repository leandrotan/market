package com.wicom.flume.sources.linkedin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;

import com.wicom.flume.sources.linkedin.feeds.FeedIsMalformedException;
import com.wicom.flume.sources.linkedin.feeds.UpdatesTimeRangeFeed;
import com.wicom.flume.utils.FileStoreHelper;
import com.wicom.flume.utils.JSONStringHelper;

import org.apache.commons.lang.exception.ExceptionUtils;

public class LinkedInUpdatesFeed extends AbstractSource implements Configurable, PollableSource {
	private static final Logger logger = LoggerFactory.getLogger(LinkedInUpdatesFeed.class);
	
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	
	private volatile long lastPoll = 0;
	private int pollInterval;
	private String types;
	private String scope;
	private FileStoreHelper lastPollHelper = new FileStoreHelper("/home/sm_user/Social_Media/Linkedin/conf/LinkedInUpdatesFeedLastPoll");
	private String selectorHeader;
	
	@Override
	public void configure(Context context)  {
		consumerKey = context.getString("consumerKey");
		pollInterval = context.getInteger("PollInterval");
		types = context.getString("types").toUpperCase();
		scope = context.getString("scope");
		consumerSecret = context.getString("consumerSecret");
		selectorHeader = context.getString("selector.header");
		String callBackURL = context.getString("callBackURL");
		try {
			logger.info("Configure LinkedIn source for Updates feed");
			Authorization auth = new Authorization(consumerKey,consumerSecret,callBackURL);
			accessToken = auth.getAccessToken();
			logger.debug("Setting up LinkedIn Updates Feed using consumer key {} and" +
					" access token {}", new String[] { consumerKey, accessToken });
	  	} catch (OAuthSystemException e) {
	  		logger.error(ExceptionUtils.getStackTrace(e));
	  	}
	}

	@Override
	public void start() {	
		try {
			lastPoll =Long.parseLong(lastPollHelper.read());
		} catch (NumberFormatException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			lastPoll=0;
		} catch (IOException e1) {
			logger.error(ExceptionUtils.getStackTrace(e1));
			lastPoll=0;
		}
		super.start();
	}

  	@Override
  	public void stop () {
  		// Disconnect from external client and do any additional cleanup
	    // (e.g. releasing resources or nulling-out field values) ..
  		try {
  			lastPollHelper.write(Long.toString(lastPoll));
  		} catch (NumberFormatException e) {
  			logger.error(ExceptionUtils.getStackTrace(e));
  		} catch (IOException e1) {
  			logger.error(ExceptionUtils.getStackTrace(e1));
  		}
		super.stop();
  	}
	  
  	@Override
  	public Status process() throws EventDeliveryException {
  		Status status = Status.BACKOFF;
  		long currentTime = System.currentTimeMillis();
  		if(TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval){
  			try {
  				JSONArray a = handleLargeUpdateSet(0,currentTime,lastPoll);
  				if (a.length()>0) {
  					logger.info("Build normal UpdatesFeed event");
  					a = JSONStringHelper.replaceNewlineChar(a);
  					Event e = EventBuilder.withBody(a.join("\n").getBytes());
	  				e.getHeaders().put(selectorHeader, "NORMAL");
	  				getChannelProcessor().processEvent(e);
  				}
  				lastPoll = currentTime;
  				status = Status.READY;
  			} catch (FeedIsMalformedException ex) {
  				logger.error(ExceptionUtils.getStackTrace(ex));
  				logger.info("Build erroneus UpdatesFeed event");
  				Event errorEvent = EventBuilder.withBody(ex.getRawResponse().getBytes());
  				errorEvent.getHeaders().put(selectorHeader, "ERROR");
  				getChannelProcessor().processEvent(errorEvent);
  				lastPoll = currentTime;
  				status = Status.READY;
  			} catch (Throwable t) {
  				status = Status.BACKOFF;
  				logger.error(ExceptionUtils.getStackTrace(t));
  				if (t instanceof Error) {
  					throw (Error)t;
  				}
  			}
  		}
  		return status;
  	}
  	
  	private JSONArray handleLargeUpdateSet(int start, long before, long after) throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
  		JSONArray res = new JSONArray();
  		UpdatesTimeRangeFeed updatesFeed = UpdatesTimeRangeFeed.process()
					.setAccessToken(accessToken)
					.setScope(scope)
					.setUpdateTypes(types)
					.setAfter(after)
					.setBefore(before)
					.setStart(start)
					.buildUpdatesFeed();
  		if (updatesFeed.getUpdatesCount()>0) {
			JSONArray updatesResponseArray = updatesFeed.getUpdatesArray();
			for (int i=0;i<updatesResponseArray.length();i++) {
				res.put(updatesResponseArray.getJSONObject(i));
			}
			int count = start+updatesFeed.getUpdatesCount();
			int total = updatesFeed.getUpdatesTotalCount();
			logger.debug("Got "+count+" updates from "+ total);
			if (count<total) {
				logger.debug("Call the same resource to get the rest of updates..");
				res = mergeUpdatesArrays(res,handleLargeUpdateSet(count+1,before,after));
			}
  		}
		return res;
  	}
  	
  	private JSONArray mergeUpdatesArrays(JSONArray arr1, JSONArray arr2) {
  		for (int i=0;i<arr2.length();i++) {
  			arr1.put(arr2.getJSONObject(i));
		}
  		return arr1;
  	}
}
