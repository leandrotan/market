package com.wicom.flume.sources.linkedin.feeds;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.OAuthAPI;

public class PostFeed extends JSONObject {
	public static final String GROUP_POST_CLASSIFIERS = "(id,type,category,creator:("+PersonFeed.BASIC_PROFILE_CLASSIFIERS+"),title,creation-timestamp,likes:(person:("+PersonFeed.BASIC_PROFILE_CLASSIFIERS+")),summary,comments:(id,creationTimestamp,text,creator:("+PersonFeed.BASIC_PROFILE_CLASSIFIERS+")))";
	
	private static final Logger logger = LoggerFactory.getLogger(PostFeed.class);
	private final static String format = "json";
	@SuppressWarnings("unused")
	private String accessToken;
	private String rawResponse;
	
	private PostFeed(String postJSON) {
		super(postJSON);
		rawResponse=postJSON;
	}
	
	private static boolean isNormal(String postJSON) {
		if (postJSON.length()==0) {
			return false;
		}
		//trying to parse as JSON
		try {
			JSONObject responseJSON = new JSONObject(postJSON);
			if (!responseJSON.has("id")) {
				return false;
			}
		} catch (JSONException e) {
			logger.debug(e.getStackTrace().toString());
			return false;
		}
		return true;
	}

	private void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getRawResponse() {
		return this.rawResponse;
	}
	
	public JSONObject getPostJSON() {
		return this;
	}
	
	public static class PostFeedBuilder {
		private String accessToken;
		private JSONObject postJSON;
		
		public PostFeedBuilder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		public PostFeedBuilder setJSONObject(String postJSONObjectString) {
			this.postJSON = new JSONObject(postJSONObjectString);
			return this;
		}
		public PostFeedBuilder setJSONObject(JSONObject postJSONObject) {
			this.postJSON = postJSONObject;
			return this;
		}
		
		public PostFeed buildPostFeedFromAPICall(String postId) throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			logger.debug("Init PostFeed for post Id: "+postId);
			final String postUrl = "https://api.linkedin.com/v1/posts/"+postId+":"+GROUP_POST_CLASSIFIERS;
			String resp = OAuthAPI.callURL(postUrl, accessToken, format);
			if (!isNormal(resp)) {
				throw new FeedIsMalformedException(resp);
			}
			PostFeed p = new PostFeed(resp);
			p.setAccessToken(this.accessToken);
			return p;
		}
		
		public PostFeed buildPostFeed() throws FeedIsMalformedException {
			if (!isNormal(postJSON.toString())) {
				throw new FeedIsMalformedException(postJSON.toString());
			}
			PostFeed p = new PostFeed(postJSON.toString());
			p.setAccessToken(this.accessToken);
			return p;
		}
	}
}
