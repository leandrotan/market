package com.wicom.flume.sources.linkedin.feeds;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.OAuthAPI;


public class UpdatesTimeRangeFeed extends JSONObject {
	
	private static final Logger logger = LoggerFactory.getLogger(UpdatesTimeRangeFeed.class);
	private final static String format = "json";
	private String accessToken;
	private String rawResponse;
	private JSONArray updates = new JSONArray();
	
	public UpdatesTimeRangeFeed(String response) {
		super(response);
		this.rawResponse = response;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken=accessToken;
	}
	
	public JSONArray getUpdatesArray() {
		if (this.getUpdatesCount()>0) {
			return this.getJSONArray("values");
		} else {
			return new JSONArray();
		}
	}
	
	public int getUpdatesCount() {
		if (this.has("values")) {
			return this.getJSONArray("values").length();
		}
		return 0;
	}
	  
	public int getUpdatesTotalCount() {
		return this.getInt("_total");
	}
	
	public String getRawResponse() {
		return this.rawResponse;
	}
	
	private void processUpdates() throws JSONException, FeedIsMalformedException, OAuthProblemException, OAuthSystemException {
		JSONArray updts = getUpdatesArray();
		for (int i=0;i<updts.length();i++) {
			UpdateFeed update = UpdateFeed.process()
								.setAccessToken(accessToken)
								.setJSONObject(updts.getJSONObject(i).toString())
								.buildUpdateFeedFromJSON();
			this.updates.put(update);
		}
	}
	
	public static UpdatesTimeRangeBuilder process() {
		return new UpdatesTimeRangeBuilder();
	}
	
	public static boolean isNormal(String resp) {
		if (resp.length()==0) {
			return false;
		}
		//trying to parse as JSON
		try {
			JSONObject responseJSON = new JSONObject(resp);
			if (!responseJSON.has("_total")) {
				return false;
			}
		} catch (JSONException e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
			return false;
		}
		return true;
	}
	

	public static class UpdatesTimeRangeBuilder {
		private String accessToken;
		private String scope;
		private String types;
		
		private long after;
		private long before;
		private long start;
		
		public UpdatesTimeRangeBuilder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		
		public UpdatesTimeRangeBuilder setScope(String scope) {
			this.scope = scope;
			return this;
		}
		
		public UpdatesTimeRangeBuilder setUpdateTypes(String types) {
			this.types = types;
			return this;
		}
		
		public UpdatesTimeRangeBuilder setAfter(long after) {
			this.after = after;
			return this;
		}
		
		public UpdatesTimeRangeBuilder setBefore(long before) {
			this.before = before;
			return this;
		}
		
		public UpdatesTimeRangeBuilder setStart(long start) {
			this.start = start;
			return this;
		}
		
		private String buildUrl() {
			StringBuilder URLBuilder = new StringBuilder("https://api.linkedin.com/v1/people/~/network/updates:"+UpdateFeed.UPDATE_CLASSIFIERS+"?count=250");
			URLBuilder.append("&after=").append(after)
			   .append("&before=").append(before)
			   .append("&start=").append(start);
			if (scope!=null&&scope.equals("self")) {
				URLBuilder.append("&scope=").append(scope);
			}
			if (types!=""){
				for (String t:types.split(",")){
					URLBuilder.append("&type=").append(t);
				}
			}
			return URLBuilder.toString();
		}
		
		public UpdatesTimeRangeFeed buildUpdatesFeed() throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			String url = buildUrl();
			logger.debug("Built URL: "+url);
			String response = OAuthAPI.callURL(url, accessToken, format);
			if (!isNormal(response)) {
				throw new FeedIsMalformedException(response);
			}
			UpdatesTimeRangeFeed f = new UpdatesTimeRangeFeed(response);
			f.setAccessToken(accessToken);
			f.processUpdates();
			return f;
		}
	}
}
