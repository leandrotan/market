package com.category;

import java.nio.charset.Charset;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import com.category.DictionaryChunker;
import com.google.common.base.Charsets;

public class GetcategoryLingpipe extends UDF {
	public static Text evaluate(final Text s)
    {  
	      String result;
		byte[] stringBytes;
		String convertedStr;
		Charset charset = Charset.forName("UTF-8"); 
		stringBytes = s.toString().getBytes(charset);
		convertedStr = new String(stringBytes, Charsets.UTF_8);
    	DictionaryChunker.initDictionary();
      if ((result =  DictionaryChunker.getEntityCategory(DictionaryChunker.dictionaryChunkerTF,convertedStr)) == "" )
    	  return new Text("Business Affairs / Other Business Affairs");
    	else 
      return new Text (result); 
     }  

}


