package com.wicom.flume.sources.linkedin.feeds;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.JSONStringHelper;
import com.wicom.flume.utils.OAuthAPI;
import com.wicom.flume.sources.linkedin.feeds.GroupFeed.GroupFeedBuilder;

public class PersonFeed extends JSONObject {
	
	public static final String BASIC_PROFILE_CLASSIFIERS = "id,firstName,lastName,headline,num-connections,location:(name,country:(code)),distance,industry,specialties,positions,current-share,apiStandardProfileRequest";
	public static final String MEMBER_PROFILE_CLASSIFIERS = "("+BASIC_PROFILE_CLASSIFIERS+",connections,summary,following:(companies:"+CompanyFeed.COMPANY_CLASSIFIERS+",industries,people),group-memberships:(group:"+GroupFeed.GROUP_CLASSIFIERS+")";
	
	private static final Logger logger = LoggerFactory.getLogger(PersonFeed.class);
	private final static String format = "json";
	private final static String personUrl = "https://api.linkedin.com/v1/people/~:"+MEMBER_PROFILE_CLASSIFIERS;
	
	private String rawPersonResponse;
	private String accessToken;
	private JSONArray companies = new JSONArray();
	private JSONArray groups = new JSONArray();
	
	private PersonFeed(String response) {
		super(response);
		this.rawPersonResponse = response;
	}
	
	public static boolean isNormal(String personJSON) {
		if (personJSON.length()==0) {
			return false;
		}
		//trying to parse as JSON
		try {
			JSONObject responseJSON = new JSONObject(personJSON);
			if (!responseJSON.has("id")) {
				return false;
			}
		} catch (JSONException e) {
			logger.debug(e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	private JSONArray getFollowingCompanies() {
		return this.getJSONObject("following").getJSONObject("companies").getJSONArray("values");
	}
	
	private void setFollowingCompanies(JSONArray companies) {
		this.getJSONObject("following").getJSONObject("companies").put("values", companies);
	}
	
	private void processCompanies() throws OAuthProblemException, OAuthSystemException, JSONException, FeedIsMalformedException {
		// This method adds person following companies feeds to PersonFeed
		JSONArray following_companies = getFollowingCompanies();
		for (int i=0;i<following_companies.length();i++) {
			CompanyFeed cmp = CompanyFeed.process()
								.setAccessToken(accessToken)
								.setJSONObject(following_companies.getJSONObject(i))
								.buildCompanyFeedFromJSON();
			companies.put(cmp.getCompanyJSON());
		}
	}
	
	private void processGroups() throws OAuthProblemException, OAuthSystemException, JSONException, FeedIsMalformedException {
		// This method adds person following companies feeds to PersonFeed
		JSONArray _groups = this.getJSONObject("groupMemberships").getJSONArray("values");
		for (int i=0;i<_groups.length();i++) {
			GroupFeed grp = new GroupFeedBuilder()
								.setAccessToken(accessToken)
								.setJSONObject(_groups.getJSONObject(i).toString())
								.buildGroupFeed();
			groups.put(grp);
		}
	}
	
	public String getRawResponse() {
		return this.rawPersonResponse;
	}
	
	private void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public static PersonFeedBuilder processFullProfile() {
		return new PersonFeedBuilder(true);
	}
	
	public JSONObject getPersonJSON() {
		//add processed groups and companies
		this.getJSONObject("groupMemberships").put("values", groups);
		this.setFollowingCompanies(companies);
		return JSONStringHelper.replaceNewlineChar(this);
	}
	
	public static class PersonFeedBuilder {
		private String accessToken;
		private JSONObject personJSON;
		private boolean fullProfile = true;
		
		public PersonFeedBuilder(boolean fullProfile) {
			this.fullProfile = fullProfile;
		}
		
		public PersonFeedBuilder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		public PersonFeedBuilder setJSONObject(String personJSONObjectString) {
			this.personJSON = new JSONObject(personJSONObjectString);
			return this;
		}
		public PersonFeedBuilder setJSONObject(JSONObject personJSONObject) {
			this.personJSON = personJSONObject;
			return this;
		}
		
		public PersonFeed buildPersonFeed() throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			if (fullProfile) {
				return buildPersonFeedFromAPICall();
			} else {
				return buildPersonFeedFromJSONObject();
			}
		}
		
		private PersonFeed buildPersonFeedFromAPICall() throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			logger.debug("Init PersonFeed with URL: "+personUrl);
			String resp = OAuthAPI.callURL(personUrl, accessToken, format);
			if (!isNormal(resp)) {
				throw new FeedIsMalformedException(resp);
			}
			PersonFeed p = new PersonFeed(resp);
			p.setAccessToken(this.accessToken);
			p.processCompanies();
			p.processGroups();
			return p;
		}
		
		private PersonFeed buildPersonFeedFromJSONObject() {
			logger.debug("Init PersonFeed with JSON: "+personJSON);
			return (PersonFeed) personJSON;
		}
	}
}
