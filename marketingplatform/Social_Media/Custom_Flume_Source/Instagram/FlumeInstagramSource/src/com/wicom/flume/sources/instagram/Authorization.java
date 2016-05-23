package com.wicom.flume.sources.instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.utils.FileStoreHelper;

public class Authorization {
	
	private static final Logger logger = LoggerFactory.getLogger(Authorization.class);
	private String longAccessToken = null;

	private String clientId;
	private String callBackURL;
	
	public Authorization(String consumerId, String consumerSecret, String callBackURL, String confFolder) throws OAuthSystemException{
		FileStoreHelper tokenHelper = new FileStoreHelper(confFolder,"Instagram.txt");
		this.clientId = consumerId;
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
                .authorizationProvider(OAuthProviderType.INSTAGRAM)
                .setClientId(clientId)
                .setRedirectURI(callBackURL)
                .setResponseType("token")
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
				logger.error(ExceptionUtils.getStackTrace(e1));
			}		
	        longAccessToken = code;
	        logger.debug("Long-lived access token: "+longAccessToken);
		
		return longAccessToken;
	}
	
	public String getAccessToken(){
		return this.longAccessToken;
	}
}
