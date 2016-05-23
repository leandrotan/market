package com.wicom.flume.sources.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.GitHubTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.FileStoreHelper;

public class Authorization {
	
	private static final Logger logger = LoggerFactory.getLogger(Authorization.class);
	
	private String shortAccessToken = null;
	private String longAccessToken = null;

	private String clientId;
	private String clientSecret;
	private String callBackURL;
	
	public Authorization(String consumerId, String consumerSecret, String callBackURL, String confFolder) throws OAuthSystemException{
		FileStoreHelper tokenHelper = new FileStoreHelper(confFolder,"FacebookToken.txt"); 
		this.clientId = consumerId;
		this.clientSecret = consumerSecret;
		this.callBackURL = callBackURL;
	    if (tokenHelper.isFileExists()){
	    	try {
				this.longAccessToken = tokenHelper.read();
			} catch (IOException e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
	    } else {
	    	this.longAccessToken = this.runAuthProcess();
	    	try {
				tokenHelper.write(this.longAccessToken);
			} catch (IOException e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
	    }
	}
	
	private String runAuthProcess() throws OAuthSystemException{
		OAuthClientRequest request = OAuthClientRequest
                .authorizationProvider(OAuthProviderType.FACEBOOK)
                .setClientId(clientId)
                .setRedirectURI(callBackURL)
                .setState(UUID.randomUUID().toString())
                .setScope("public_profile,user_friends,user_about_me,user_activities,user_events,user_groups,user_likes,user_location,user_photos,read_friendlists,read_stream,manage_pages")
                .buildQueryMessage();

            //in web application you make redirection to uri:
            System.out.println("Visit: " + request.getLocationUri() + "\nand grant permission");

            System.out.print("Now enter the OAuth code you have received in redirect uri ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String code = "";
			try {
				code = br.readLine();
			} catch (IOException e1) {
				logger.error(ExceptionUtils.getStackTrace(e1));
			}
		
		request = OAuthClientRequest
                .tokenProvider(OAuthProviderType.FACEBOOK)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(callBackURL)
                .setCode(code)
                .setScope("public_profile,user_friends,user_about_me,user_activities,user_events,user_groups,user_likes,user_location,user_photos,read_friendlists,read_stream,manage_pages")
                .buildQueryMessage();
		
		//create OAuth client that uses custom http client under the hood
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        System.out.println(request.getLocationUri());
        GitHubTokenResponse oAuthResponse;
		try {
			oAuthResponse = oAuthClient.accessToken(request, GitHubTokenResponse.class);
	        shortAccessToken = oAuthResponse.getAccessToken();
	        logger.debug("Short-lived access token: "+shortAccessToken);
		} catch (OAuthProblemException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} 
		
		request = OAuthClientRequest
                .tokenProvider(OAuthProviderType.FACEBOOK)
                .setParameter("grant_type", "fb_exchange_token")
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(callBackURL)
                .setParameter("fb_exchange_token",shortAccessToken)
                .buildQueryMessage();
		
		//create OAuth client that uses custom http client under the hood
        oAuthClient = new OAuthClient(new URLConnectionClient());
        System.out.println(request.getLocationUri());
		try {
			oAuthResponse = oAuthClient.accessToken(request, GitHubTokenResponse.class);
	        longAccessToken = oAuthResponse.getAccessToken();
	        logger.debug("Long-lived access token: "+longAccessToken);
		} catch (OAuthProblemException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		return longAccessToken;
	}
	
	public String getAccessToken(){
		return this.longAccessToken;
	}
}
