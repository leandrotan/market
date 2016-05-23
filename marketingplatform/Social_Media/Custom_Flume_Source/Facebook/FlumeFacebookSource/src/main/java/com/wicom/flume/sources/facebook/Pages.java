package com.wicom.flume.sources.facebook;

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
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.sources.facebook.utils.APIUtils;
import com.wicom.flume.utils.JSONStringHelper;

public class Pages extends AbstractSource implements Configurable,
		PollableSource {

	private static final Logger logger = LoggerFactory
			.getLogger(Pages.class);

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
	private String confFolder;
	
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
				Map<String,JSONObject> pages = APIUtils.getPagesInfo(pageNames,accessToken,oAuthClient);
				Event e;
				for (JSONObject page: pages.values()) {
					page.put("timestamp_ms", currentTime);
					e = EventBuilder.withBody(JSONStringHelper.replaceNewlineChar(page).toString().getBytes());
					e.getHeaders().put(selectorHeader, "NORMAL");
					getChannelProcessor().processEvent(e);
				}
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
}
