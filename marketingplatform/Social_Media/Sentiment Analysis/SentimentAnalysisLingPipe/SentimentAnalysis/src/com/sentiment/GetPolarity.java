package com.sentiment;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class GetPolarity extends UDF{

	static SentimentClassifier sentClassifier = new SentimentClassifier();
    static String sent;
    
    public Text evaluate(final Text s)
    {  
      if (s == null)
      { return null; }  
      
      System.out.println();
      return new Text(sentClassifier.classify(s.toString())); 
     }    
}
