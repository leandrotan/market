package com.wicom.flumeSources;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public class Authorization {
	private String accessToken = null;
	private static String refreshToken = null;
	private String ClientId;
	private String ClientSecret;
	private static String access="/home/sm_user/Social_Media/Youtube/bin/youTubeAccessToken.txt";
	private String refresh="/home/sm_user/Social_Media/Youtube/bin/youTubeRefreshToken.txt";
	public Authorization(String ClientId,String ClientSecret) throws OAuthSystemException{
	    this.ClientId = ClientId;
		this.ClientSecret = ClientSecret;
	    if (isFileExists(access)){
	    	try {
				accessToken = read(access);
				if(!accessToken.equals("")){
				if( !isTokenValid(accessToken))
				{
					accessToken = "";
				}}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } else {
	    	accessToken = runAuthProcess();
	    	try {
	             write(accessToken,access);
                 write(refreshToken,refresh);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	private String runAuthProcess() throws OAuthSystemException{
		String scopes = "https://www.googleapis.com/auth/yt-analytics.readonly https://www.googleapis.com/auth/youtube.readonly";
		OAuthClientRequest request = OAuthClientRequest
                .authorizationProvider(OAuthProviderType.GOOGLE)
                .setClientId(ClientId)
                .setRedirectURI("urn:ietf:wg:oauth:2.0:oob")
                .setResponseType("code")
                .setScope(scopes)
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
		//create OAuth client that uses custom http client under the hood
		String[] jsonObject = {"access_token","refresh_token"};
	    String [] Tokens = getJsonObject("https://accounts.google.com/o/oauth2/token","client_secret=" + ClientSecret + "&grant_type=authorization_code&redirect_uri=urn%3Aietf%3Awg%3Aoauth%3A2.0%3Aoob&code=" + code + "&client_id=" + ClientId,jsonObject);
		accessToken = Tokens[0];
		refreshToken = Tokens [1];
		return accessToken;
	}
	
	public String getAccessToken(){
		if (accessToken.isEmpty())
		{
		try{
				String[] requestedJson = {"access_token"};
				refreshToken = read(refresh);
				String[] tokens = getJsonObject("https://accounts.google.com/o/oauth2/token","client_secret=" + URLEncoder.encode(ClientSecret) + "&grant_type=refresh_token&refresh_token=" + URLEncoder.encode(refreshToken) + "&client_id=" + URLEncoder.encode(ClientId) ,requestedJson);
				accessToken = tokens[0];
			    System.out.println(accessToken);
			    write(accessToken,access);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return accessToken;
	}
	
	/** Write content to the given file. */
	  static void write(String token,String S) throws IOException  {
	    Writer out = new OutputStreamWriter(new FileOutputStream(S));
	    try {
	      out.write(token);
	    }
	    finally {
	      out.close();
	    }
	  }
	  
	  /** Read the contents of the given file. */
	  static String read(String S) throws IOException {
	    StringBuilder text = new StringBuilder();
	    Scanner scanner = new Scanner(new FileInputStream(S));
	    try {
	      while (scanner.hasNextLine()){
	        text.append(scanner.nextLine());
	      }
	    }
	    finally{
	      scanner.close();
	    }
	    return text.toString();
	  }
	  
	  public static boolean isFileExists(String S){
		  File file = new File(S);
		  return file.exists();
	  }
	public boolean isTokenValid(String S)
	{
        boolean valid = true;
        String[] jsonObject = {"expires_in"};
	    String [] Tokens = getJsonObject("https://accounts.google.com/o/oauth2/tokeninfo","access_token=" + S,jsonObject);
	    String expiry = "value:" + Tokens[0];
 
		    if (expiry.equals("value:null")) 
		    {  
		    	valid = false;
		    }
		return valid;
	}
	
	public String[] getJsonObject(String url,String urlParam,String[] S)
	{   String[] R =  new String[2];
		try {
	        URL obj = new URL(url);
	        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			String urlParameters = urlParam;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String resp = response.toString().replace('"','\'');
			JSONObject json = (JSONObject) JSONSerializer.toJSON(resp);    
			for (int i=0; i <S.length; i++)
			    {
				 if(json.containsKey(S[i]))
				   {
					   R[i]=json.getString(S[i]);
				   }
				   else R[i]="";

			    }
	}catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	catch(NullPointerException  e)
    {e.printStackTrace();
    }
	return R;	
    }

}
