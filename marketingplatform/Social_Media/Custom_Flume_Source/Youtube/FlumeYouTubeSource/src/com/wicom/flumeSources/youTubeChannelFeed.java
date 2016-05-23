package com.wicom.flumeSources;

import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.EventDrivenSource;
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
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class youTubeChannelFeed extends AbstractSource implements EventDrivenSource,Configurable, PollableSource{
	  
      private static final Logger logger = LoggerFactory.getLogger(YouTubeSubsFeed.class);
	  private String consumerKey;
	  private String consumerSecret;
	  OAuthResourceResponse resourceResponse;
	  String resourceResponse1;
	  com.google.api.client.auth.oauth2.Credential Cred;
	  private volatile long lastPoll = 0;
	  private int pollInterval;
      public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
      public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	  private String accessToken;
	  private OAuthClient oAuthClient;
	  OAuthClientRequest bearerClientRequest;
	  String selectorHeader;


	  @Override
	  public void configure(Context context)  {
		  
		  consumerKey = context.getString("consumerKey");
          consumerSecret = context.getString("consumerSecret");
		  pollInterval = context.getInteger("PollInterval");
	  	  selectorHeader = context.getString("selector.header");
			try {
				logger.debug("Configure YouTube source");
			    Authorization auth = new Authorization(consumerKey,consumerSecret);
				accessToken = auth.getAccessToken();
				logger.debug("Setting up YouTube sample using consumer key {} and" +
				          " access token {}", new String[] { consumerKey, accessToken });
				oAuthClient = new OAuthClient(new URLConnectionClient());
			} catch (OAuthSystemException e) {
				e.printStackTrace();
		    }
	  }

	  @Override
	  public void start() {
		  super.start();
	  }

	  @Override
	  public void stop () {
		    logger.debug("Shutting down...");
		    super.stop();
	  }
	  
	  @Override
	  public Status process() throws EventDeliveryException {
		  Status status = Status.BACKOFF;
		  getChannel();
	      status = Status.READY;
		  return status;
	  }
	  
	  public void getChannel() {
		  long currentTime = System.currentTimeMillis();
		  String value = "";
		  if(TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval){
			  try {
				   
				    bearerClientRequest = new OAuthBearerClientRequest("https://www.googleapis.com/youtube/v3/channels?part=id%2C+snippet%2C+contentDetails%2C+statistics%2C+topicDetails%2C+invideoPromotion&maxResults=50&mine=true&access_token=" + accessToken)
				    .buildQueryMessage();
					bearerClientRequest.addHeader("x-li-format", "json");
					logger.debug(bearerClientRequest.getLocationUri());
				
						resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
						value = resourceResponse.getBody().replace("\r\n", "").replace("\n","");
						if(value.contains("error"))
						{Event e = EventBuilder.withBody(value.getBytes());
						e.getHeaders().put(selectorHeader, "ERROR");
						getChannelProcessor().processEvent(e);}
						else{
						Event e = EventBuilder.withBody(value.getBytes());
						e.getHeaders().put(selectorHeader, "Channel");
						getChannelProcessor().processEvent(e);
						JSONObject json = (JSONObject) JSONSerializer.toJSON(resourceResponse.getBody().toString());     
						
						while(json.containsKey("nextPageToken"))
						   {
							bearerClientRequest = new OAuthBearerClientRequest("https://www.googleapis.com/youtube/v3/channels?part=id%2C+snippet%2C+contentDetails%2C+statistics%2C+topicDetails%2C+invideoPromotion&mine=true&maxResults=50&access_token=" + accessToken + "&pageToken=" + json.getString("nextPageToken"))
						    .buildQueryMessage();
							bearerClientRequest.addHeader("x-li-format", "json");
							logger.debug(bearerClientRequest.getLocationUri());
						
								resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
								if(value.contains("error")){
								    e = EventBuilder.withBody(value.getBytes());
									e.getHeaders().put(selectorHeader, "ERROR");
									getChannelProcessor().processEvent(e);}else{
								value =  resourceResponse.getBody().replace("\r\n", "").replace("\n","");
								e = EventBuilder.withBody(value.getBytes());
								e.getHeaders().put(selectorHeader, "Channel");
								getChannelProcessor().processEvent(e);
							json = (JSONObject) JSONSerializer.toJSON(resourceResponse.getBody().toString());   
						  }}
						
						}	
						lastPoll = currentTime;
					}

					catch(Exception e){  
						    e.printStackTrace();
							Event e1 = EventBuilder.withBody(e.toString().getBytes());
							e1.getHeaders().put(selectorHeader, "ERROR");
							getChannelProcessor().processEvent(e1);  
					}	
			  }
		
	}
}
