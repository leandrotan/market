package com.wicom.flume.sources.instagram;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.JSONStringHelper;
import com.wicom.flume.sources.instagram.feeds.FeedJSON;

public class Instagram_Feed extends AbstractSource implements Configurable,
		PollableSource {

	private static final Logger logger = LoggerFactory
			.getLogger(Instagram_Feed.class);

	private String client_id;//CLIENT ID
	private String accessToken;
	private String client_secret;//CLIENT SECRET
	private String callBackURL; 

	private OAuthClient oAuthClient;
	OAuthClientRequest bearerClientRequest;
	OAuthResourceResponse resourceResponse;

	private volatile long lastPoll = 0;
	private int pollInterval;
	private long since;
	private String confFolder;
	
	public final static String BASE_URL = "https://api.instagram.com/v1/";

	//@Override
	public void configure(Context context) {
		client_id = context.getString("consumerKey");
		client_secret = context.getString("consumerSecret");
		pollInterval = context.getInteger("PollInterval");
		callBackURL = context.getString("callBackURL");
		confFolder = context.getString("confFolder");
		since = LocalDateTime.now().minusMonths(context.getInteger("monthesFrame")).toDate().getTime()/1000;
		try {
			logger.info("Configure Instagram Feed source");
			Authorization auth = new Authorization(client_id,client_secret,callBackURL,confFolder);
			accessToken = auth.getAccessToken();
			oAuthClient = new OAuthClient(new URLConnectionClient());
		} catch (OAuthSystemException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		// Disconnect from external client and do any additional cleanup
		super.stop();
	}

	//@Override
	public Status process() throws EventDeliveryException {
		Status status = Status.BACKOFF;
		long currentTime = System.currentTimeMillis();

		if (TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval) {
			try {
				String URL = BASE_URL+"users/self/feed?count="+20;
				processFeed(URL,since,currentTime);
				lastPoll = currentTime;
				status = Status.READY;
			} catch (Throwable t) {
				// Log exception, handle individual exceptions as needed
				status = Status.BACKOFF;
				logger.error(ExceptionUtils.getStackTrace(t));
				// re-throw all Errors
				if (t instanceof Error) {
					throw (Error) t;
				}
			}
		}
		return status;
	}
	
	private String callFeedAPI(String url) throws OAuthSystemException {
		String res = "";
		bearerClientRequest = new OAuthBearerClientRequest(url)
			.setAccessToken(accessToken)
			.buildQueryMessage();
		logger.debug(bearerClientRequest.getLocationUri());
		try {
			resourceResponse = oAuthClient.resource(
					bearerClientRequest, OAuth.HttpMethod.GET,
					OAuthResourceResponse.class);
			// This try clause includes whatever Channel operations you
			// want to do Receive new data
			res = resourceResponse.getBody();
		} catch (OAuthProblemException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return res;
	}
	
	private void processFeed(String url, long since, long current_timestamp) throws OAuthSystemException {
		String response = callFeedAPI(url);
		//validate response
		if (!FeedJSON.isNormal(response)) {
			//do smth
		}
		FeedJSON feed = new FeedJSON(response);
		//TODO: add logic to extract advanced profile info for each user here
		
		//extract posts and put the to the Channel as separate events
		JSONArray posts = feed.getPosts();
		Event e;
		for (int i=0;i<=posts.length()-1;i++) {
			//TODO: rewrite using PostJSON object instead??
			JSONObject post = posts.getJSONObject(i);
			post.put("timestamp_ms", current_timestamp);
			e = EventBuilder.withBody(JSONStringHelper.replaceNewlineChar(post).toString().getBytes());
			e.getHeaders().put("timestamp", post.getString("created_time")+"000");
			getChannelProcessor().processEvent(e);
		}
		if (feed.getLastPostTimestamp()>=since&&feed.getPostsCount()!=0) {
			logger.debug("Going down!");
			processFeed(feed.getNextUrl(),since,current_timestamp);
		}
	}
}