package com.wicom.flume.sources.linkedin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.sources.linkedin.feeds.FeedIsMalformedException;
import com.wicom.flume.sources.linkedin.feeds.PersonFeed;
import com.wicom.flume.utils.FileStoreHelper;

public class LinkedInPersonFeed extends AbstractSource implements Configurable, PollableSource  {
	private static final Logger logger = LoggerFactory.getLogger(LinkedInPersonFeed.class);
	
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private int pollInterval;
	
	private volatile long lastPoll = 0;
	private FileStoreHelper lastPollHelper = new FileStoreHelper("/home/sm_user/Social_Media/Linkedin/conf/LinkedInPersonLastPoll");
	private String selectorHeader;
	
	@Override
	public void configure(Context context)  {
		logger.info("Configuring Group feed source");
		consumerKey = context.getString("consumerKey");
		pollInterval = context.getInteger("PollInterval");
		consumerSecret = context.getString("consumerSecret");
		String callBackURL = context.getString("callBackURL");
		selectorHeader = context.getString("selector.header");
		try {
			logger.debug("Configure LinkedIn source for Group feed");
			Authorization auth = new Authorization(consumerKey,consumerSecret,callBackURL);
			accessToken = auth.getAccessToken();
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
		if (TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval) {
			try {
				PersonFeed person = PersonFeed.processFullProfile()
									.setAccessToken(accessToken)
									.buildPersonFeed();
				//add timestamp to json file (divide on 1000 to remove milliseconds from it)
				person.put("ts", Math.round(currentTime/1000));
				//replace \n symbols in json string before push the event
				Event e = EventBuilder.withBody(person.getPersonJSON().toString().getBytes());
				e.getHeaders().put(selectorHeader, "NORMAL");
				getChannelProcessor().processEvent(e);
				lastPoll = currentTime;
				status = Status.READY;
			} catch (FeedIsMalformedException e) {
				logger.error(ExceptionUtils.getStackTrace(e));
				logger.info("Process erroneus PersonFeed event. Check Errors subdirectory");
				String r = e.getRawResponse();
				Event errorEvent = EventBuilder.withBody(r.getBytes());
				errorEvent.getHeaders().put(selectorHeader, "ERROR");
				getChannelProcessor().processEvent(errorEvent);
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
}
