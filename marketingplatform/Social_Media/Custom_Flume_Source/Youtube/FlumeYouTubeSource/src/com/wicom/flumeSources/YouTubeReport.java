package com.wicom.flumeSources;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtubeAnalytics.YouTubeAnalytics;
import com.google.api.services.youtubeAnalytics.model.ResultTable;

public class YouTubeReport extends AbstractSource implements EventDrivenSource,Configurable, PollableSource{
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
	  OAuthClientRequest bearerClientRequest;
	  String selectorHeader;
	  String startDate = "";
	  String endDate = "";
	  String metrics = "";
	  String sort = "";
	  String dimension = "";
	  String header = "";
	    private static YouTubeAnalytics analytics;

      @Override
	  public void configure(Context context)  {
    	   Date date = new Date();
    	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		  consumerKey = context.getString("consumerKey");
          consumerSecret = context.getString("consumerSecret");
		  pollInterval = context.getInteger("PollInterval");
	  	  selectorHeader = context.getString("selector.header");
	  	  startDate = context.getString("startDate");
	  	  endDate = context.getString("endDate");
	  	  if (startDate.compareToIgnoreCase("today")==0)
	  	  {
	  		startDate = sdf.format(date);
	  	  }
	  	if (endDate.compareToIgnoreCase("today")==0)
	  	{
	  		endDate = sdf.format(date);
	  	} 
	  	  metrics = context.getString("metrics");
	  	  dimension = context.getString("dimension");
	  	  header = context.getString("header");
	  	  sort = context.getString("sort");
			try {
				logger.debug("Configure YouTube source");
			    Authorization auth = new Authorization(consumerKey,consumerSecret);
				accessToken = auth.getAccessToken();
				logger.debug("Setting up YouTube sample using consumer key {} and" +
				          " access token {}", new String[] { consumerKey, accessToken });
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
		  getReport();
	      status = Status.READY;
		  return status;
	  }
	  private void getReport() {
		  long currentTime = System.currentTimeMillis();
		  String value = "";
		  analytics = new YouTubeAnalytics.Builder(HTTP_TRANSPORT, JSON_FACTORY,null).setApplicationName("YouTubeAPI").build();
		  ResultTable RT;
		  if(TimeUnit.MILLISECONDS.toSeconds(currentTime - lastPoll) > pollInterval){ 
				  try {
						 RT = analytics.reports()
						    .query("channel==mine",                          // channel id
				     	               startDate,                              // Start date.
				     	               endDate,                               // End date.
				     	              metrics) // Metrics.
						    .setDimensions(dimension)
						    .setSort(sort)
			     	        .setMaxResults(50)
			     	        .setOauthToken(accessToken).execute();

						if(RT.containsKey("error"))
						{Event e = EventBuilder.withBody(RT.toString().getBytes());
						e.getHeaders().put(selectorHeader, "ERROR");
						getChannelProcessor().processEvent(e);}
						else{
							value = printData(RT);
							Event e = EventBuilder.withBody(value.getBytes());
							e.getHeaders().put(selectorHeader, header);
							getChannelProcessor().processEvent(e);  
	
						}
						lastPoll = currentTime;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Event e1 = EventBuilder.withBody(e.toString().getBytes());
						e1.getHeaders().put(selectorHeader, "ERROR");
						getChannelProcessor().processEvent(e1);  
					}
			  }
		
	}
	  
	  
	  private static String printData( ResultTable results) {
	        String value = "";
	        if (results.getRows() == null || results.getRows().isEmpty()) {
	          System.out.println("No results Found.");
	        } else {

	          // Print column headers.
	         /* for (ColumnHeaders header : results.getColumnHeaders()) {
	        	 value = value + "\t" + header.getName();
	          }
	          value = value.replaceFirst("\t", "")  + "\n";*/
	          // Print actual data.
	          for (List<Object> row : results.getRows()) {
	           Object column = row.get(0);
	           value  = value + column.toString();
	            for (int colNum = 1; colNum < results.getColumnHeaders().size(); colNum++) {
	              column = row.get(colNum);
	              value  = value + "\t" + column;
	            }
	            value  = value + "\n";
	          }
	          value  = value + "\n";
	        }
	        
	        return value.toLowerCase().replaceFirst("\\n\\s+$", "");
	      }

  
}