package com.wicom.flumeSources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

public class YouTubeSearchFeed extends AbstractSource implements EventDrivenSource,Configurable, PollableSource {
	private static final Logger logger = LoggerFactory.getLogger(YouTubeSearchFeed.class);
	
	  private String consumerKey;
	  private String consumerSecret;
	  private String KeyWords;
	  OAuthResourceResponse resourceResponse;
	  String resourceResponse1;
	  com.google.api.client.auth.oauth2.Credential Cred;
	  private volatile static long lastPoll = 0;
	  private volatile String lastPollinSec = "0";
	  private static int pollInterval;
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
		  KeyWords = context.getString("KeyWords");
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
		  getSearch();
	      status = Status.READY;
		  return status;
	  }
	  

	public void getSearch()
	  {

		  long currentTime = System.currentTimeMillis();
	      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      Date now = new Date();
	      String strDate = sdfDate.format(now).replace(" ", "T")+ "Z"; 
		  if(TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval){
			  try {
				   
				  	if(lastPollinSec.equals("0")){
				  	bearerClientRequest = new OAuthBearerClientRequest("https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&maxResults=50&publishedBefore="+strDate+"&access_token=" + accessToken + "&q=" + KeyWords)
				    .buildQueryMessage();}
				  	else
				    bearerClientRequest = new OAuthBearerClientRequest("https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&maxResults=50&publishedAfter="+strDate+ "&access_token=" + accessToken + "&q=" + KeyWords)
				    .buildQueryMessage();
					bearerClientRequest.addHeader("x-li-format", "json");
					logger.debug(bearerClientRequest.getLocationUri());
				
						resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
						String value = resourceResponse.getBody().replace("\r\n", "").replace("\n","");
						if(value.contains("error"))
						{Event e = EventBuilder.withBody(value.getBytes());
						e.getHeaders().put(selectorHeader, "ERROR");
						getChannelProcessor().processEvent(e);}
						else{
						Event e = EventBuilder.withBody(value.getBytes());
						e.getHeaders().put(selectorHeader, "Search");
						getChannelProcessor().processEvent(e);
						/*JSONObject json = (JSONObject) JSONSerializer.toJSON(resourceResponse.getBody().toString());     
						
							while(json.containsKey("nextPageToken") && !lastPollinSec.equals("0"))
							   {
								bearerClientRequest = new OAuthBearerClientRequest("https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&maxResults=25&pageToken=" + json.getString("nextPageToken") + "&publishedAfter="+lastPollinSec+ "&access_token=" + accessToken + "&q=" + KeyWords)
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
									e.getHeaders().put(selectorHeader, "NORMAL");
									getChannelProcessor().processEvent(e);
								json = (JSONObject) JSONSerializer.toJSON(resourceResponse.getBody().toString());   
							  }}*/}
							  lastPoll = currentTime;
				              lastPollinSec = strDate;
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
