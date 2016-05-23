package com.wicom.flumeSources;

import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
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

public class YouTubeVideoComments  extends AbstractSource implements EventDrivenSource,Configurable, PollableSource{
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
		  getVideoComments();
	      status = Status.READY;
		  return status;
	  }
	  
	  private String getVideoComments() {
		  long currentTime = System.currentTimeMillis();
		  String value = "";
		  
		   		
		  if(TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval){
			  try {
				  
				    bearerClientRequest = new OAuthBearerClientRequest("https://www.googleapis.com/youtube/v3/channels?part=id%2C+snippet%2C+contentDetails%2C+statistics%2C+topicDetails%2C+invideoPromotion&maxResults=50&mine=true&access_token=" + accessToken).buildQueryMessage();
					bearerClientRequest.addHeader("x-li-format", "json");
					logger.debug(bearerClientRequest.getLocationUri());
				    resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
				    JSONObject ChanDetsJson = (JSONObject) JSONSerializer.toJSON(resourceResponse.getBody().toString());
				    JSONArray JA = ChanDetsJson.getJSONArray("items");
				    String uploads = JA.getJSONObject(0).getJSONObject("contentDetails").getJSONObject("relatedPlaylists").getString("uploads");
				    bearerClientRequest = new OAuthBearerClientRequest("https://www.googleapis.com/youtube/v3/playlistItems?part=contentDetails&maxResults=50&playlistId=" + uploads + "&access_token=" + accessToken).buildQueryMessage();
					bearerClientRequest.addHeader("x-li-format", "json");
				    resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
				    JSONObject VideoListJson = (JSONObject) JSONSerializer.toJSON(resourceResponse.getBody().toString());
				    JSONArray JAVL = VideoListJson.getJSONArray("items");
				    String[] VideoIds = new String[JAVL.size()];
				    for(int i = 0 ; i < JAVL.size() ; i++){
				    	VideoIds[i]= (JAVL.getJSONObject(i).getJSONObject("contentDetails").getString("videoId"));
				    	  bearerClientRequest = new OAuthBearerClientRequest("https://gdata.youtube.com/feeds/api/videos/" + VideoIds[i] + "/comments?alt=json")
						    .buildQueryMessage();
							bearerClientRequest.addHeader("x-li-format", "json");
							logger.debug(bearerClientRequest.getLocationUri());
								resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
								value = resourceResponse.getBody().replace("\r\n", "").replace("\n","").replace("$", "hdp");
								 if(value.contains("error"))
									{Event e = EventBuilder.withBody(value.getBytes());
									e.getHeaders().put(selectorHeader, "ERROR");
									getChannelProcessor().processEvent(e);}
									else{
									Event e = EventBuilder.withBody(value.getBytes());
									e.getHeaders().put(selectorHeader, "VideoComments");
									getChannelProcessor().processEvent(e);
				    }
						}	
						lastPoll = currentTime;
					}

					catch(Exception e){  
						    e.printStackTrace();
						    Event ev = EventBuilder.withBody(value.getBytes());
							ev.getHeaders().put(selectorHeader, "Error");
							getChannelProcessor().processEvent(ev);
					}	
			  }
		return value;  
		
	}
}
