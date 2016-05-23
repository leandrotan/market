package com.wicom.flume.sources.linkedin.feeds;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.sources.linkedin.feeds.PostFeed.PostFeedBuilder;
import com.wicom.flume.utils.OAuthAPI;

public class GroupFeed extends JSONObject {
	
	public static final String GROUP_CLASSIFIERS = "(id,name,posts:"+PostFeed.GROUP_POST_CLASSIFIERS+"),membership-state)";
	
	private static final Logger logger = LoggerFactory.getLogger(GroupFeed.class);
	private final static String format = "json";
	private String accessToken;
	private String rawGroupResponse;
	private JSONArray posts = new JSONArray();
	
	private GroupFeed(String groupJSON) {
		super(groupJSON);
		rawGroupResponse=groupJSON;
	}

	private static boolean isNormal(String groupJSON) {
		if (groupJSON.length()==0) {
			return false;
		}
		//trying to parse as JSON
		try {
			JSONObject responseJSON = new JSONObject(groupJSON);
			if (!responseJSON.has("group")) {
				return false;
			}
		} catch (JSONException e) {
			logger.debug(e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	private void processGroupPosts() throws JSONException, FeedIsMalformedException {
		JSONObject grpPostsObj = this.getJSONObject("group").getJSONObject("posts");
		if (grpPostsObj.getInt("_total")>0) {
			JSONArray grp_posts = grpPostsObj.getJSONArray("values");
			for (int i=0;i<grp_posts.length();i++) {
				PostFeed post = new PostFeedBuilder()
									.setAccessToken(this.accessToken)
									.setJSONObject(grp_posts.getJSONObject(i))
									.buildPostFeed();
				posts.put(post.getPostJSON());
			}
		}
	}
	
	public String getRawResponse() {
		return this.rawGroupResponse;
	}
	
	private void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public static class GroupFeedBuilder {
		private String accessToken;
		private JSONObject groupJSON;
		
		public GroupFeedBuilder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		public GroupFeedBuilder setJSONObject(String groupJSONString) {
			this.groupJSON = new JSONObject(groupJSONString);
			return this;
		}

		public GroupFeed buildGroupFeedFromAPICall(String groupId) throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			logger.debug("Init GroupFeed for group Id: "+groupId);
			final String groupUrl = "https://api.linkedin.com/v1/groups/"+groupId+":"+GROUP_CLASSIFIERS;
			String resp = OAuthAPI.callURL(groupUrl, accessToken, format);
			if (!isNormal(resp)) {
				throw new FeedIsMalformedException(resp);
			}
			GroupFeed p = new GroupFeed(resp);
			p.setAccessToken(this.accessToken);
			p.processGroupPosts();
			return p;
		}
		
		public GroupFeed buildGroupFeed() throws FeedIsMalformedException {
			if (!isNormal(groupJSON.toString())) {
				throw new FeedIsMalformedException(groupJSON.toString());
			}
			GroupFeed p = new GroupFeed(groupJSON.toString());
			p.setAccessToken(this.accessToken);
			p.processGroupPosts();
			return p;
		}
	}
}
