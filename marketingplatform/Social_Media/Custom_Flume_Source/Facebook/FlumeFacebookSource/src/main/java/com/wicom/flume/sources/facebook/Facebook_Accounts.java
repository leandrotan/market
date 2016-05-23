package com.wicom.flume.sources.facebook;

import java.util.concurrent.TimeUnit;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.wicom.flume.sources.facebook.feeds.AccountsJSON;
import com.wicom.flume.utils.JSONStringHelper;

public class Facebook_Accounts extends AbstractSource implements Configurable,
		PollableSource {

	private static final Logger logger = LoggerFactory
			.getLogger(Facebook_Accounts.class);

	private String consumerKey;
	private String accessToken;
	private String consumerSecret;
	private String callBackURL;
	private String selectorHeader;

	private OAuthClient oAuthClient;
	OAuthClientRequest bearerClientRequest;
	OAuthResourceResponse resourceResponse;

	private volatile long lastPoll = 0;
	private int pollInterval;
	private String confFolder;
	
	@Override
	public void configure(Context context) {
		consumerKey = context.getString("consumerKey");
		consumerSecret = context.getString("consumerSecret");
		pollInterval = context.getInteger("PollInterval");
		callBackURL = context.getString("callBackURL");
		selectorHeader = context.getString("selector.header");
		confFolder = context.getString("confFolder");
		try {
			logger.debug("Configure Facebook Accounts source");
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
		super.stop();
	}

	@Override
	public Status process() throws EventDeliveryException {
		Status status = Status.BACKOFF;
		long currentTime = System.currentTimeMillis();
		if (TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval) {
			try {
				String field_classifiers = "id,name,feed{id,from,message,type,status_type,object_id,created_time,updated_time,likes,comments{likes,from,id,created_time,comment_count,like_count,message}},likes,tagged";
				bearerClientRequest = new OAuthBearerClientRequest(
						"https://graph.facebook.com/v2.2/me/accounts?fields="+field_classifiers)
						.setAccessToken(accessToken)
						.buildQueryMessage();
				bearerClientRequest.addHeader("x-li-format", "json");
				logger.debug("Call URL: "+bearerClientRequest.getLocationUri());
				try {
					resourceResponse = oAuthClient.resource(
							bearerClientRequest, OAuth.HttpMethod.GET,
							OAuthResourceResponse.class);
					String respBody = resourceResponse.getBody();
					Event e;
					if (AccountsJSON.isNormal(respBody)) {
						//process normal request
						AccountsJSON a = new AccountsJSON(respBody);
						a.addTimestamp(currentTime);
						e = EventBuilder.withBody(JSONStringHelper.replaceNewlineChar(a).toString().getBytes());
						e.getHeaders().put(selectorHeader, "NORMAL");
					} else {
						// process erroneous request
						e = EventBuilder.withBody(respBody.getBytes());
						e.getHeaders().put(selectorHeader, "ERROR");
					}
					getChannelProcessor().processEvent(e);
					lastPoll = currentTime;
					status = Status.READY;
				} catch (OAuthProblemException e) {
					logger.error(ExceptionUtils.getStackTrace(e));
				}
			} catch (Throwable t) {
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
