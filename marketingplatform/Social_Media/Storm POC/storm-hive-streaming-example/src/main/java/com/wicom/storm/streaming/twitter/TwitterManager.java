package com.wicom.storm.streaming.twitter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TwitterManager {
     
	SentimentClassifier sentClassifier;
	static Calendar c = Calendar.getInstance(); 

	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	static String dateStart = null;
    static String dateEnd = null;
    static Date date1 = null;
    static Date date2 = null;
    private static final Logger logger = LoggerFactory.getLogger(TwitterManager.class);

	public TwitterManager() {

		sentClassifier = new SentimentClassifier();
	}
	
	
	
	public void performQueryOverHadoopTweets() throws InterruptedException, IOException
	{
	String driverName = "org.apache.hive.jdbc.HiveDriver";

	try {
	      Class.forName(driverName);
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	    }
	    Connection con = null;
	    Statement stmt=null;
	    Statement stmt1=null;
	    Statement stmt2=null;
	    ResultSet res=null;
		try {
			
			logger.debug("Trying to get connection");
			con = DriverManager.getConnection("jdbc:hive2://nex-hdp-14.nexius.com:10000/wrd10_socialmedia", "sm_user", "hdp-08/home");
			logger.debug("Connected");
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			stmt2 = con.createStatement();

			String sent = "";
		    String sql = "select id,id_str,text,day_key from wrd10_socialmedia.twitter_publicstream_orc where day_key >= '" + dateStart + "'  and day_key <= '"+dateEnd+"'";
		    res = stmt.executeQuery(sql);
		    sql = "use wrd10_socialmedia";
		    stmt2.executeUpdate(sql);
		    sql = "set hive.exec.dynamic.partition.mode=nonstrict";
		    stmt2.executeUpdate(sql);
			c.setTime(date1);
			sql = "ALTER TABLE tweetsentiments DROP IF EXISTS PARTITION(day_key ='" +  formatter.format(c.getTime())+ "')";
			stmt2.executeUpdate(sql);
			while (c.getTime().compareTo(date2) < 0){
			    c.add(Calendar.DATE, 1);
			    sql = "ALTER TABLE tweetsentiments DROP IF EXISTS PARTITION(day_key ='" +  formatter.format(c.getTime())+ "')";
				stmt2.executeUpdate(sql);
}
		    int category=-1;
		    DictionaryChunker.initDictionary();
		    while (res.next()){
		    	sql = "from (";
		    for (int i=0; res.next() && i < 1000 ; i++){
		    	String id = res.getString(1);
		    	String id_str = res.getString(2);
				String text = res.getString(3);
				String day = res.getString(4);
				if(text != null && !text.isEmpty())
				{	sent = sentClassifier.classify(text);
				if (sent.equalsIgnoreCase("Negative")) category = 0;
				else if (sent.equalsIgnoreCase("Neutral")) category = 1;
				else if (sent.equalsIgnoreCase("Positive")) category = 2;
				String entity = DictionaryChunker.getEntity(DictionaryChunker.dictionaryChunkerTF,text);
				String entityCategory = DictionaryChunker.getEntityCategory(DictionaryChunker.dictionaryChunkerTF,text);
				sql =  sql + " select " + id + " as id,'"+id_str+"' as id_str,"+category+" as sentiment_category,'" +entity+"' as entity,'"+entityCategory+"' as entity_subject,  '" + day + "' as day_key UNION ALL ";
				}
				}				    
		    
		    if (sql.contains("UNION ALL")){
			    int endIndex = sql.lastIndexOf("UNION ALL");
                sql  = sql.substring(0, endIndex); 
			    sql = sql + ") j  insert into table wrd10_socialmedia.tweetsentiments PARTITION(day_key) select j.*";
			    stmt1.executeUpdate(sql);
		    }
		}
		    
		    }
		   catch (Exception e) {
	e.printStackTrace();
}				
		 finally {
		        try { res.close(); } catch (Exception e) { /* ignored */ }
		        try { stmt.close();stmt1.close();stmt2.close(); } catch (Exception e) { /* ignored */ }
		        try { con.close(); } catch (Exception e) { /* ignored */ }		    
} 
}

	public static void main(String[] args)
	{
			try{ 
	   TwitterManager twitterManager = new TwitterManager();  
		dateStart = args[0];
		dateEnd = args[1];
				try {
			    date1 = formatter.parse(dateStart);
			    date2 = formatter.parse(dateEnd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		twitterManager.performQueryOverHadoopTweets();
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

}