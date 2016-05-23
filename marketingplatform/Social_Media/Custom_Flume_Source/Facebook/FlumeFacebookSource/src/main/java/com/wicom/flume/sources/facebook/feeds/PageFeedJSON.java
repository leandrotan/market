package com.wicom.flume.sources.facebook.feeds;

import org.json.JSONException;
import org.json.JSONObject;

import com.wicom.flume.sources.facebook.Facebook_Pages;

public class PageFeedJSON extends JSONObject {
	public PageFeedJSON(String body) {
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
	
	public boolean hasNext() {
		if ((this.getJSONArray("data").length()>0)&&(this.has("paging"))&&(this.getJSONObject("paging").has("next"))) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getNextURL() {
		return this.getJSONObject("paging").getString("next").replace(Facebook_Pages.BASE_URL, "");
	}
	
	public void setPageId() {
		
	}
	
	public void setPageName() {
		
	}
	
	public boolean isError() {
		return this.has("error");
	}
	
	public boolean isErrorTransient() {
		return this.getJSONObject("error").getBoolean("is_transient");
	}
	
	public int getErrorCode() {
		return this.getJSONObject("error").getInt("code");
	}
	
	public String getErrorMessage() {
		return this.getJSONObject("error").getString("message");
	}
}
