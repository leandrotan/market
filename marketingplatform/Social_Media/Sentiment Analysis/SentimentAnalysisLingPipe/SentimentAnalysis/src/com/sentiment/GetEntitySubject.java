package com.sentiment;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class GetEntitySubject extends UDF{
    public static Text evaluate(final Text s)
    {  
    	
    	 DictionaryChunker.initDictionary();
      if (s == null)
      { return null; }  
      String result;
      if ((result =  DictionaryChunker.getEntityCategory(DictionaryChunker.dictionaryChunkerTF,s.toString())) == "" )
    	  return new Text("Business Affairs / Other Business Affairs");
    	else 
      return new Text (result); 
     }  
    
  /* public static void main(String args[])
    {
    System.out.println(evaluate(new Text("ش    الشبكه بتطلع وتنزل  ")).toString());
    }*/
}
