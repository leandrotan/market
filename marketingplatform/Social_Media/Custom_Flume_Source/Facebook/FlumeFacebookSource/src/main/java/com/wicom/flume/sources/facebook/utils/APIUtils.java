package com.wicom.flume.sources.facebook.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.sources.facebook.Pages;

/**
 * 
 * This class is a helper for facebook API calls.
 * It contains some common calls which can be used in different agents
 */

public class APIUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(APIUtils.class);
	
	
	/**
	 * Makes a separate call to REST API to get basic pages info. 
	 * Returns array of responses (for each page from pageNames array)
	 */
	public static Map<String,JSONObject> getPagesInfo(String[] pageNames, String accessToken, OAuthClient oAuthClient) throws OAuthSystemException {
		
		JSONArray r = new JSONArray();
		for (String pageId: pageNames) {
			JSONObject requestItem = new JSONObject();
			requestItem.put("method", "GET");
			requestItem.put("relative_url", pageId);
			r.put(requestItem);
		}
		
		OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(Pages.BASE_URL+"?batch="+r.toString()+"&method=POST")
				.setAccessToken(accessToken)
				.buildQueryMessage();
		
		logger.debug(bearerClientRequest.getLocationUri());
		Map<String,JSONObject> res = new HashMap<String,JSONObject>();
		try {
			OAuthResourceResponse resourceResponse = oAuthClient.resource(
					bearerClientRequest, OAuth.HttpMethod.POST,
					OAuthResourceResponse.class);
			JSONArray raw = new JSONArray(resourceResponse.getBody());
			for (int i=0;i<raw.length();i++) {
				res.put(pageNames[i], new JSONObject(raw.getJSONObject(i).getString("body")));
			}
		} catch (OAuthProblemException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (JSONException e1) {
			logger.error(ExceptionUtils.getStackTrace(e1));
			throw(e1);
		}
		return res;
	}
}
