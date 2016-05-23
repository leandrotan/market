package com.wicom.flume.utils;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public class OAuthAPI {
	public static String callURL(String Url,String accessToken,String format) throws OAuthProblemException,OAuthSystemException {
		OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(Url)
		.buildQueryMessage();
		bearerClientRequest.addHeader("x-li-format", format);
		bearerClientRequest.addHeader("Authorization", "Bearer "+accessToken);
		OAuthResourceResponse resourceResponse = new OAuthClient(new URLConnectionClient()).resource(bearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
		// Receive new data
		return resourceResponse.getBody();
  }
}
