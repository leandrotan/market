package com.wicom.flume.sources.instagram.feeds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Mykhail Martsynyuk
 * Class represents single response from Instagram users/self/feed endpoint
 * See https://instagram.com/developer/endpoints/users/#get_users_feed
 * 
 * Purpose of the class to validate response and provide high level methods 
 * for further processing (if needed)
 * 
 */

public class FeedJSON extends JSONObject {
	public FeedJSON(String body) {
		super(body);
	}
	
	public static boolean isNormal(String body) {
		try {
			JSONObject t = new JSONObject(body);
			if (t.has("data")) {
				return true;
			}
		} catch (JSONException e) {
			return false;
		}
		return false;
	}
	
	public int getNextMaxId() {
		return this.getJSONObject("pagination").getInt("next_max_id");
	}
	
	public String getNextUrl() {
		return this.getJSONObject("pagination").getString("next_url");
	}
	
	public String getMetaCode() {
		return this.getJSONObject("meta").getString("code");
	}
	
	public void addTimestamp(long timestamp) {
		this.put("timestamp_ms", timestamp);
	}
	
	public long getLastPostTimestamp() {
		JSONArray posts = this.getJSONArray("data");
		return posts.getJSONObject(posts.length()-1).getLong("created_time");
	}
	
	public int getPostsCount() {
		return this.getJSONArray("data").length();
	}
	
	public JSONArray getPosts() {
		return this.getJSONArray("data");
	}
}
