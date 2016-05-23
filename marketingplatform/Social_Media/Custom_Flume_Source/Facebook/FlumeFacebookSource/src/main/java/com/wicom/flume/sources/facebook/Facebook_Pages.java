package com.wicom.flume.sources.facebook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
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

import com.wicom.flume.sources.facebook.feeds.PageFeedJSON;
import com.wicom.flume.sources.facebook.utils.APIUtils;
import com.wicom.flume.sources.facebook.utils.BatchRequestPagesFeed;
import com.wicom.flume.utils.JSONStringHelper;

public class Facebook_Pages extends AbstractSource implements Configurable,
		PollableSource {

	private static final Logger logger = LoggerFactory
			.getLogger(Facebook_Pages.class);

	private String consumerKey;
	private String accessToken;
	private String consumerSecret;
	private String callBackURL;
	private String[] pageNames;
	private String selectorHeader;

	private OAuthClient oAuthClient;
	OAuthClientRequest bearerClientRequest;
	OAuthResourceResponse resourceResponse;

	private volatile long lastPoll = 0;
	private int pollInterval;
	private long since;
	private String confFolder;
	private int postsLimit;
	
	private Map<String,JSONObject> initialPages;
	
	public final static String BASE_URL = "https://graph.facebook.com/v2.3/";

	@Override
	public void configure(Context context) {
		consumerKey = context.getString("consumerKey");
		consumerSecret = context.getString("consumerSecret");
		pollInterval = context.getInteger("PollInterval");
		callBackURL = context.getString("callBackURL");
		pageNames = context.getString("pageNames").split(",");
		selectorHeader = context.getString("selector.header");
		confFolder = context.getString("confFolder");
		postsLimit = context.getInteger("postsLimit");
		since = LocalDateTime.now().minusMonths(context.getInteger("monthesFrame")).toDate().getTime()/1000;
		try {
			logger.info("Configure Facebook Pages source");
			Authorization auth = new Authorization(consumerKey,consumerSecret,callBackURL,confFolder);
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

	@Override
	public Status process() throws EventDeliveryException {
		Status status = Status.BACKOFF;
		long currentTime = System.currentTimeMillis();

		if (TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval) {
			try {
				initialPages = APIUtils.getPagesInfo(pageNames,accessToken,oAuthClient);
				String URL = BASE_URL+"?batch=";
				BatchRequestPagesFeed batchReq = new BatchRequestPagesFeed(pageNames,since,postsLimit);
				logger.debug("Generated URL: "+URL+batchReq.generateBatchRequest());
				processMultiplePagesFeed(batchReq,currentTime);
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
	
	/**
	 * Recursive call to @callAPIPagesFeedByCursorURL depending on amount of data returned
	 * @param batchReq
	 * @param timestamp_ms
	 * @throws OAuthSystemException
	 */
	private void processMultiplePagesFeed (BatchRequestPagesFeed batchReq, long timestamp_ms) throws OAuthSystemException {
		boolean doNextProcess = false;
		boolean hasErrors = false;
		String URL = BASE_URL+"?batch=";
		logger.debug("Generated URL: "+URL+batchReq.generateBatchRequest());
		ArrayList<PageFeedJSON> pageFeeds = callAPIPagesFeedByCursorURL(URL+batchReq.generateBatchRequest());
		DateFormat post_created_time_format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.ENGLISH);
		for (int i=0;i<pageFeeds.size();i++) {
			PageFeedJSON pageFeed = pageFeeds.get(i);
			JSONObject page = initialPages.get(batchReq.getPageIds().get(i));
			Event e;
			if (pageFeed.isError()) {
				e = EventBuilder.withBody(pageFeed.toString().getBytes());
				e.getHeaders().put(selectorHeader, "ERROR");
				getChannelProcessor().processEvent(e);
				hasErrors = true;
			} else {
				JSONArray posts = pageFeed.getJSONArray("data");
				for (int j=0;j<posts.length();j++) {
					JSONObject post=posts.getJSONObject(j);
					post.put("page", page);
					post.put("timestamp_ms", timestamp_ms);
					e = EventBuilder.withBody(JSONStringHelper.replaceNewlineChar(post).toString().getBytes());
					e.getHeaders().put(selectorHeader, "NORMAL");
					try {
						Date created_time = post_created_time_format.parse(post.getString("created_time"));
						e.getHeaders().put("timestamp", String.valueOf(created_time.getTime()));
					} catch (ParseException e1) {
						logger.error(ExceptionUtils.getStackTrace(e1));
						e.getHeaders().put(selectorHeader, "ERROR");
					}
					getChannelProcessor().processEvent(e);
				}
				if ((!hasErrors)&&pageFeed.hasNext()) {
					doNextProcess = true;
				}
			}
		}
		if (doNextProcess) {
			processMultiplePagesFeed(new BatchRequestPagesFeed(pageFeeds,since),timestamp_ms);
		}
	}
	
	/**
	 * Makes a call to REST API with batch URL. Returns array of responses
	 * @param url
	 * @return ArrayList<PageFeedJSON>
	 * @throws OAuthSystemException
	 */
	private ArrayList<PageFeedJSON> callAPIPagesFeedByCursorURL(String url) throws OAuthSystemException {
		// returns array of PageJSON object based on url
		bearerClientRequest = new OAuthBearerClientRequest(url+"&method=POST")
				.setAccessToken(accessToken)
				.buildQueryMessage();

		logger.debug(bearerClientRequest.getLocationUri());
		ArrayList<PageFeedJSON> res = new ArrayList<PageFeedJSON>();
		try {
			resourceResponse = oAuthClient.resource(
					bearerClientRequest, OAuth.HttpMethod.POST,
					OAuthResourceResponse.class);
			// This try clause includes whatever Channel operations you
			// want to do Receive new data
			JSONArray raw = new JSONArray(resourceResponse.getBody());
			for (int i=0;i<raw.length();i++) {
				res.add(new PageFeedJSON(raw.getJSONObject(i).getString("body")));
			}
		} catch (OAuthProblemException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return res;
	}
}
