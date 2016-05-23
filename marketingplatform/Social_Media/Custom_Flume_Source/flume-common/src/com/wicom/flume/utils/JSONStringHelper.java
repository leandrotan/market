package com.wicom.flume.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONStringHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(JSONStringHelper.class);
	
	public static String replaceNewlineChar(String body) {
  		logger.debug("Replacing new line characters in status json");
		String newBody = body.replace("\\n", " ").replace("\\r", "");
		return newBody;
  	}
	
	public static JSONArray replaceNewlineChar(JSONArray arr) {
  		logger.debug("Replacing new line characters in json array");
  		for (int i=0; i<arr.length();i++) {
  			String newBody = arr.getJSONObject(i).toString().replace("\\n", " ").replace("\\r", "");
  			arr.put(i, new JSONObject(newBody));
  		}
  		return arr;
  	}
	
	public static JSONObject replaceNewlineChar(JSONObject obj) {
  		logger.debug("Replacing new line characters in json object");
		String newBody = obj.toString().replace("\\n", " ").replace("\\r", "");
		return new JSONObject(newBody);
  	}
	
	public static boolean hasTag(String json, String tag) {
		return new JSONObject(json).has(tag);
	}
}
