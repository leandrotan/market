package com.wicom.flume.sources.linkedin.feeds;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.OAuthAPI;

/**
 *  Represents single Update object.
 *  Can be initialized from people update keys or company update keys as well as UpdateJSON from any parent feed if it passed validation
 */

public class UpdateFeed extends JSONObject {
	private static final Logger logger = LoggerFactory.getLogger(UpdateFeed.class);
	private final static String format = "json";
	private String accessToken;
	private String rawUpdateResponse;
	
	public static final String UPDATE_COMMENT_CLASSIFIERS = "(id,comment,person:("+PersonFeed.BASIC_PROFILE_CLASSIFIERS+"),sequenceNumber,timestamp)";
	public static final String UPDATE_LIKE_CLASSIFIERS = "(person:("+PersonFeed.BASIC_PROFILE_CLASSIFIERS+"))";
	public static final String UPDATE_CLASSIFIERS = "(updateKey,updateType,isCommentable,isLikable,isLiked,numLikes,timestamp,updateContent:(person:("+PersonFeed.BASIC_PROFILE_CLASSIFIERS+",connections),updateAction,company,companyJobUpdate,companyStatusUpdate),updateComments:"+UPDATE_COMMENT_CLASSIFIERS+",likes:"+UPDATE_LIKE_CLASSIFIERS+")";
	
	private UpdateFeed(String updateJSON) {
		super(updateJSON);
		this.rawUpdateResponse = updateJSON;
	}
	
	public static UpdateFeedBuilder process() {
		return new UpdateFeedBuilder();
	}
	
	private static boolean isNormal(String updateJSON) {
		if (updateJSON.length()==0) {
			return false;
		}
		//trying to parse as JSON
		try {
			JSONObject responseJSON = new JSONObject(updateJSON);
			if (!responseJSON.has("updateKey")) {
				return false;
			}
		} catch (JSONException e) {
			logger.debug(e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	
	private void setUpdateLikes() throws JSONException, OAuthProblemException, OAuthSystemException {
		if (this.getBoolean("isLikable")) {
			String updateKey = this.getString("updateKey");
			final String likesTagName = "likes";
			int totalLikes = this.optInt("numLikes",0);
			if (totalLikes>0&&(this.getJSONObject(likesTagName).getInt("_total")<totalLikes)) {
				JSONObject likes;
				String likesUrl = "https://api.linkedin.com/v1/people/~/network/updates/key="+updateKey+"/likes:"+UPDATE_LIKE_CLASSIFIERS;
				likes = new JSONObject(OAuthAPI.callURL(likesUrl, accessToken, format));
				this.put(likesTagName,likes);
		  	}
		}
	}
	
	private void setUpdateComments() throws OAuthProblemException, OAuthSystemException{
		if (this.getBoolean("isCommentable")) {
			String updateKey = this.getString("updateKey");
			logger.debug("Processing comments for update key: "+updateKey);
			final String commentsTagName = "updateComments";
			int totalComments = this.getJSONObject(commentsTagName).getInt("_total");
			if (totalComments>0) {
				String updatesUrl = "https://api.linkedin.com/v1/people/~/network/updates/key="+updateKey+"/update-comments:"+UPDATE_COMMENT_CLASSIFIERS;
				JSONObject comments = new JSONObject(OAuthAPI.callURL(updatesUrl,accessToken,format));
				this.put(commentsTagName,comments);
			}
		}
	}
	
	public String getRawResponse() {
		return this.rawUpdateResponse;
	}
	
	private void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public static class UpdateFeedBuilder {
		private String accessToken;
		private String updateJSONString;
		private String companyId;
		private String updateId;
		
		public UpdateFeedBuilder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		public UpdateFeedBuilder setJSONObject(String updateJSONString) {
			this.updateJSONString = updateJSONString;
			return this;
		}
		public UpdateFeedBuilder setCompanyId(String companyId) {
			this.companyId = companyId;
			return this;
		}
		public UpdateFeedBuilder setUpdateId(String updateId) {
			this.updateId = updateId;
			return this;
		}
		
		public UpdateFeed buildUpdateFeedFromAPICall() throws OAuthProblemException, OAuthSystemException, FeedIsMalformedException {
			String url = "https://api.linkedin.com/v1/companies/"+companyId+"/updates/key="+updateId;
			String resp = OAuthAPI.callURL(url, accessToken, format);
			if (!isNormal(resp)) {
				throw new FeedIsMalformedException(resp);
			}
			UpdateFeed p = new UpdateFeed(resp);
			p.setAccessToken(this.accessToken);
			p.setUpdateLikes();
			p.setUpdateComments();
			return p;
		}
		
		public UpdateFeed buildUpdateFeedFromJSON() throws FeedIsMalformedException, JSONException, OAuthProblemException, OAuthSystemException {
			if (!isNormal(updateJSONString)) {
				throw new FeedIsMalformedException(updateJSONString);
			}
			UpdateFeed p = new UpdateFeed(updateJSONString);
			p.setAccessToken(this.accessToken);
			p.setUpdateLikes();
			p.setUpdateComments();
			return p;
		}
	}
}
