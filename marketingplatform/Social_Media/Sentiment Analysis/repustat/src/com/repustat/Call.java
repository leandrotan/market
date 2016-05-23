package com.repustat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class Call {
	
	static String text = "";
	static String apikey = "469d503f9e0c7664203a6f2db81145a17da88184";
	static String writePath = "writing";
	static String readPath = "reading";
	static String topics = "good";
	static String Type = "";
	static String lang = "ar";
	static String niche = "telecom";
	static String category = "";
	static String emoji = "0";
    static String query = "";
	static List<String> Lines = new ArrayList<>();
	static List<String> Lines2 = new ArrayList<>();
	static byte[] dataBytes = null;
	public static void main(String args[])
        {
		Call c = new Call();
		c.getpropertyValues();
		readFile(readPath);
		if (Type.equalsIgnoreCase("score"))
		   score();
		else if (Type.equalsIgnoreCase("bulkscore"))
		bulkScore();
		else if (Type.equalsIgnoreCase("topics"))
		{   if (topics.isEmpty())
		      {topics = "nothing";}
			getTopic(topics);}
		else if (Type.equalsIgnoreCase("categorize"))
			getCategorization();
		else if (Type.equalsIgnoreCase("addrule"))
			addRule();
		else if (Type.equalsIgnoreCase("getEntities"))
			getEntities();
		else
		getChunks();
        }

	private void getpropertyValues() {
		Properties prop = new Properties();
		String propFileName = "config.properties";
		 
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		 try{
		if (inputStream != null) {
			prop.load(inputStream);
			// get the property value and print it out
		     apikey = prop.getProperty("apikey");
			 writePath = prop.getProperty("writePath");
			 readPath = prop.getProperty("readPath");
			 topics = prop.getProperty("topics");
			 Type = prop.getProperty("type");
			 lang = prop.getProperty("lang");
			 niche = prop.getProperty("niche");
			 category = prop.getProperty("category");
			 query = prop.getProperty("query");
			 emoji = prop.getProperty("emoji");
		}
		else
		{
		throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath \n so the default values will be used, writepath as writing adn the readPath is reading topics as good");
		} 
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		}

	// posts a url with certain parameters
	public static String postURL (String url,String[] urlparams)
	{  
        HttpURLConnection connection = null; 
        String urlParams = urlparams[0];

	    try {
	    	//Write the urlParameters
	    	    for (int i=1; i< urlparams.length; i++)
	    	    {
	    	    	urlParams = urlParams + "&" + urlparams[i];
	    	    }

	        //Create connection
			URL URL = new URL (url);
	        connection = (HttpURLConnection)URL.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
	        connection.setUseCaches (false);
	        connection.setDoInput(true);
	        connection.setDoOutput(true);

	        //Send request
	        DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
	        wr.writeBytes (urlParams);
	        wr.flush ();
	        wr.close ();
	        //Get Response 
	        InputStream is = connection.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is,"UTF-8"));
	        String line;
	        StringBuffer response = new StringBuffer(); 
	        while((line = rd.readLine()) != null) {
	          response.append(line);
	          response.append('\r');
	        }
	        rd.close();
	        return response.toString();

	      } catch (Exception e) {

	        e.printStackTrace();
	        return null;

	      } finally {

	        if(connection != null) {
	          connection.disconnect(); 
	        }
	      }
	}
	
	//reads the file from any path
	public static void readFile(String path)
	{
		File file = new File(path);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;
	    InputStreamReader isr = null;
	    BufferedReader br = null;
	    String sCurrentLine = "";
	    try {
	      fis = new FileInputStream(file);
	      // Here BufferedInputStream is added for fast reading.
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);
	      isr = new InputStreamReader(dis, "UTF-8");
	      br = new BufferedReader(isr);
	      // check until the line is empty
	      while ((sCurrentLine = br.readLine()) != null) {
               text = text + sCurrentLine + "\r\n";
	      }
	      // dispose all the resources after using them.
	      fis.close();
	      bis.close();
	      dis.close();
	      isr.close();
	      br.close();

	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
	}
	
	// writes any content sent to the specified path
	public static void writeToFile(String path,String content)
	{
		try {
			File file = new File(path);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			/*
			OutputStream os = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(os,"UTF-8");
			osw.write(content);
			osw.close();
			os.close();
			*/
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(path,true), "UTF-8"));
			bw.write(content);
			bw.close();
			fw.close();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void splitbyperiod(String content)
	{
		String substring;
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(content);
		int start = iterator.first();
		for (int end = iterator.next();
		    end != BreakIterator.DONE;
		    start = end, end = iterator.next()) {
			substring = content.substring(start,end);
			Lines.add(substring);
		}
	}
	
	public static void splitbynewline(String content)
	{
		String textStr[] = content.split("[\\r\\n]+");
		
		for (int i = 0;i < textStr.length; i++)
		{
			Lines2.add(textStr[i]);
		}
	}
	
	//gets the score of a certain text
	public static void score()
	{
		String[] urlParameters = new String[3];
		urlParameters[1] = "lang=" + lang;
		urlParameters[2] = "emoji=" + emoji;
		String response = "";
		//splitbyperiod(text);
		//Lines.add(text);
		splitbynewline(text);
		for (String l : Lines2) {
			try {
				urlParameters[0] = "text=" + URLEncoder.encode(l, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String url = "http://api.repustate.com/v3/" + apikey + "/score.json";
			response = postURL(url,urlParameters);
	        response = org.apache.commons.lang.StringEscapeUtils.unescapeJava(response);
			writeToFile(writePath,l + ":\t" + response + "\n");

		}	
	}
	
	
	//get a bulk score of a file
	public static void bulkScore()
	{
		String[] urlParameters = new String[3];
		String response = "";
		String texts = "";
		int i = 1;
		urlParameters[2] = "emoji=" + emoji;
		urlParameters[1] = "lang=" + lang;
		splitbyperiod(text);
		try {
		for (String l : Lines) {
           texts = texts + "text" + i + "=" + URLEncoder.encode(l, "UTF-8") + "&"; 
           writeToFile(writePath, "text" + i + ":\t" + l + "\n");
	        i++;
		}
		texts = texts.substring(0, texts.length()-1);
	    urlParameters[0] = texts ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "http://api.repustate.com/v3/" + apikey + "/bulk-score.json";
		response = postURL(url,urlParameters);
        response = org.apache.commons.lang.StringEscapeUtils.unescapeJava(response);
		writeToFile(writePath, response + "\n");
		
	}

	//get the topic 
	public static void getTopic(String topics)
	{		
    String[] urlParameters = new String[3];
	urlParameters[1] = "topics=" + topics;
	urlParameters[2] = "lang=" + lang;
    String response = "";
	splitbyperiod(text);
	for (String l : Lines) {
		try {
			urlParameters[0] = "text=" + URLEncoder.encode(l, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "http://api.repustate.com/v3/" + apikey + "/topic.json";
		response = postURL(url,urlParameters);
        response = org.apache.commons.lang.StringEscapeUtils.unescapeJava(response);
		writeToFile(writePath,l + ":\t" + response + "\n");
	}		
	}
	
	//get chunks
	private static void getChunks() {
		String[] urlParameters = new String[2];
		String response = "";
		
			try {
				urlParameters[0] = "text=" + URLEncoder.encode(text, "UTF-8");
				urlParameters[1] = "lang=" + URLEncoder.encode(lang, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String url = "http://api.repustate.com/v3/" + apikey + "/chunk.json";
			response = postURL(url,urlParameters);
			response = org.apache.commons.lang.StringEscapeUtils.unescapeJava(response);
			writeToFile(writePath, response + "\n");
	}

	//get the Categorization
	public static void getCategorization()
	{		
    String[] urlParameters = new String[3];
	try {
		urlParameters[1] = "lang=" + URLEncoder.encode(lang, "UTF-8");
		urlParameters[2] = "niche=" + URLEncoder.encode(niche,"UTF-8");
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    String response = "";
    splitbynewline(text);
	for (String l : Lines2) {
		try {
			urlParameters[0] = "text=" + URLEncoder.encode(l, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = "http://api.repustate.com/v3/" + apikey + "/categorize.json";
		response = postURL(url,urlParameters);
        response = org.apache.commons.lang.StringEscapeUtils.unescapeJava(response);
		writeToFile(writePath, l + "\n" + response + "\n");
	}		
	}
	
	//add rules to the categorizations
	private static void addRule() {
		String[] urlParameters = new String[3];
		String response = "";
		
			try {
				urlParameters[0] = "niche=" + URLEncoder.encode(niche, "UTF-8");
				urlParameters[1] = "category=" + URLEncoder.encode(category, "UTF-8");
				urlParameters[2] = "query=" + URLEncoder.encode(query, "UTF-8");
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String url = "http://api.repustate.com/v3/" + apikey + "/category-rules.json";
			response = postURL(url,urlParameters);
			response = org.apache.commons.lang.StringEscapeUtils.unescapeJava(response);
			writeToFile(writePath, response + "\n");
	}
	
	
	
	//gets the entities of a certain text
		public static void getEntities()
		{
			String[] urlParameters = new String[2];
			urlParameters[1] = "lang=" + lang;
			String response = "";
			//splitbyperiod(text);
			//Lines.add(text);
			splitbynewline(text);
			for (String l : Lines2) {
				try {
					urlParameters[0] = "text=" + URLEncoder.encode(l, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String url = "http://api.repustate.com/v3/" + apikey + "/entities.json";
				response = postURL(url,urlParameters);
		        response = org.apache.commons.lang.StringEscapeUtils.unescapeJava(response);
				writeToFile(writePath, response + "\n");

			}	
		}
	
	
}


	
	
	
