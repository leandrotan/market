package com.wicom.flume.sources.linkedin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import java.util.UUID;

import com.wicom.flume.utils.FileStoreHelper;

public class Authorization {
	private String accessToken = null;

	private String clientId;
	private String clientSecret;
	private String callBackURL;
	
	public Authorization(String consumerId, String consumerSecret, String callBackURL) throws OAuthSystemException{
		FileStoreHelper tokenHelper = new FileStoreHelper("/home/sm_user/Social_Media/Linkedin/conf/LinkedInAccessToken.txt"); 
		this.clientId = consumerId;
		this.clientSecret = consumerSecret;
		this.callBackURL = callBackURL;
	    if (tokenHelper.isFileExists()){
	    	try {
				this.accessToken = tokenHelper.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } else {
	    	this.accessToken = this.runAuthProcess();
	    	try {
				tokenHelper.write(this.accessToken);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	private String runAuthProcess() throws OAuthSystemException{
		OAuthClientRequest request = OAuthClientRequest
                .authorizationProvider(OAuthProviderType.LINKEDIN)
                .setClientId(clientId)
                .setRedirectURI(callBackURL)
                .setResponseType("code")
                .setState(UUID.randomUUID().toString())
                .buildQueryMessage();

            //in web application you make redirection to uri:
            System.out.println("Visit: " + request.getLocationUri() + "\nand grant permission");

            System.out.print("Now enter the OAuth code you have received in redirect uri ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String code = "";
			try {
				code = br.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		request = OAuthClientRequest
                .tokenProvider(OAuthProviderType.LINKEDIN)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(callBackURL)
                .setCode(code)
                .buildQueryMessage();
		
		//create OAuth client that uses custom http client under the hood
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        System.out.println(request.getLocationUri());
        OAuthJSONAccessTokenResponse oAuthResponse;
		try {
			oAuthResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);
	        accessToken = oAuthResponse.getAccessToken();
	        System.out.println(accessToken);
	        Long expiresIn = oAuthResponse.getExpiresIn();
	        System.out.println(expiresIn);
	        
		} catch (OAuthProblemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return accessToken;
	}
	
	public String getAccessToken(){
		return this.accessToken;
	}
}
