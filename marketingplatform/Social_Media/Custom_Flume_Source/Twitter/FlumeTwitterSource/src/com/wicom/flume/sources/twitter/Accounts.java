package com.wicom.flume.sources.twitter;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
//import twitter4j.internal.org.json.JSONObject;
import org.json.JSONObject;
import twitter4j.json.DataObjectFactory;
import twitter4j.TwitterFactory;

import com.wicom.flume.utils.JSONStringHelper;

public class Accounts extends AbstractSource implements Configurable,
  PollableSource {

 private static final Logger logger = LoggerFactory
   .getLogger(Accounts.class);

 private String consumerKey;
 private String accessToken;
 private String consumerSecret;
 private String selectorHeader;

 OAuthClientRequest bearerClientRequest;
 OAuthResourceResponse resourceResponse;

 private volatile long lastPoll = 0;
 private int pollInterval;
 //public final static String BASE_URL = "https://graph.facebook.com/v2.3/";

 private String accessTokenSecret;
 private Twitter twitter;
 private String users;
 private String userScreenNames;
 
 @Override
 public void configure(Context context) {
		consumerKey = context.getString("consumerKey");
	    consumerSecret = context.getString("consumerSecret");
	    accessToken = context.getString("accessToken");
	    accessTokenSecret = context.getString("accessTokenSecret");
	    selectorHeader = context.getString("selector.header");
	    
	    users = context.getString("users");
	    userScreenNames = context.getString("usersScreenName");
	    pollInterval = context.getInteger("PollInterval");
	    
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setOAuthConsumerKey(consumerKey);
	    cb.setOAuthConsumerSecret(consumerSecret);
	    cb.setOAuthAccessToken(accessToken);
	    cb.setOAuthAccessTokenSecret(accessTokenSecret);
	    cb.setJSONStoreEnabled(true);
	    cb.setIncludeEntitiesEnabled(true);
	    cb.setDebugEnabled(false);
	    cb.setUseSSL(true);
	    twitter = new TwitterFactory(cb.build()).getInstance();
	   
	    try {
	    	   logger.info("Configure Twittt source");
	    	   twitter = new TwitterFactory(cb.build()).getInstance();
	    	  } catch (Exception e) {
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
	  //User user = twitter.showUser(users); // if the REST is called by user_id
	   
	   String[] userScreenName_parts = userScreenNames.split(",");
	   for (int i=0; i<userScreenName_parts.length; i++) {
			
			String userScreenName = userScreenName_parts[i].trim();
			User user = twitter.showUser(userScreenName);
			//int followersCount= user.getFollowersCount();
			//String raw  = "{\"user_id\": " +users+ ","+"\"followers_count\": " + followersCount+"}";
			
			JSONObject user_response = new JSONObject(DataObjectFactory.getRawJSON(user));						
			
			Event e;
			if (user_response.has("followers_count")) {
				//process normal request
				user_response.put("timestamp_ms", currentTime);
				e = EventBuilder.withBody(JSONStringHelper.replaceNewlineChar(user_response).toString().getBytes());
				e.getHeaders().put(selectorHeader, "NORMAL");
			} else {
				// process erroneous request
				e = EventBuilder.withBody(JSONStringHelper.replaceNewlineChar(user_response).toString().getBytes());
				e.getHeaders().put(selectorHeader, "ERROR");
			}
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