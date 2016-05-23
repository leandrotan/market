package com.wicom.flume.sources.facebook.feeds;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountsJSON extends JSONObject {
	public AccountsJSON(String body) {
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
	
	public void addTimestamp(long timestamp) {
		this.put("timestamp_ms", timestamp);
	}
}
