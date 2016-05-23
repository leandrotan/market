package com.wicom.flume.sources.linkedin.feeds;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.OAuthAPI;

public class CompanyFeed extends JSONObject {
	
	public static final String COMPANY_CLASSIFIERS = "(id,name,company-type,description,twitter-id,num-followers)";
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyFeed.class);
	private final static String format = "json";
	
	private String rawCompanyResponse;
	@SuppressWarnings("unused")
	private String accessToken;
	
	private CompanyFeed(String response) {
		super(response);
		this.rawCompanyResponse = response;
	}
	
	public static CompanyFeedBuilder process() {
		return new CompanyFeedBuilder();
	}
	
	public static boolean isNormal(String rawResponse) {
		if (rawResponse.length()==0) {
			return false;
		}
		//trying to parse as JSON
		try {
			JSONObject responseJSON = new JSONObject(rawResponse);
			if (!responseJSON.has("id")) {
				return false;
			}
		} catch (JSONException e) {
			logger.debug(e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	public String getRawResponse() {
		return this.rawCompanyResponse;
	}
	
	public JSONObject getCompanyJSON() {
		return (JSONObject)this;
	}
	
	private void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public static class CompanyFeedBuilder {
		
		private String accessToken;
		private JSONObject companyJSON;
		private String companyId;
		
		public CompanyFeedBuilder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		public CompanyFeedBuilder setJSONObject(String companyJSONObjectString) {
			this.companyJSON = new JSONObject(companyJSONObjectString);
			return this;
		}
		public CompanyFeedBuilder setJSONObject(JSONObject companyJSONObject) {
			this.companyJSON = companyJSONObject;
			return this;
		}
		public CompanyFeedBuilder setCompanyId(String companyId) {
			this.companyId = companyId;
			return this;
		}
		
		public CompanyFeed buildCompanyFeedFromAPICall() throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			String url = "https://api.linkedin.com/v1/companies/"+companyId+":"+COMPANY_CLASSIFIERS;
			logger.debug("Init CompanyFeed with URL: "+url);
			String resp = OAuthAPI.callURL(url, accessToken, format);
			if (!isNormal(resp)) {
				throw new FeedIsMalformedException(resp);
			}
			CompanyFeed p = new CompanyFeed(resp);
			p.setAccessToken(this.accessToken);
			return p;
		}
		
		public CompanyFeed buildCompanyFeedFromJSON() throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			logger.debug("Init CompanyFeed with JSON");
			if (!isNormal(companyJSON.toString())) {
				throw new FeedIsMalformedException(companyJSON.toString());
			}
			CompanyFeed p = new CompanyFeed(companyJSON.toString());
			p.setAccessToken(this.accessToken);
			return p;
		}
	}
}
