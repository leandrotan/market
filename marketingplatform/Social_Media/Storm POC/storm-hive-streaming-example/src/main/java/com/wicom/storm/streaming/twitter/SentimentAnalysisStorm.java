package com.wicom.storm.streaming.twitter;

public class SentimentAnalysisStorm {
	
	static SentimentClassifier sentClassifier;
    static String sent;

	public static int getPolarity(String text)
	{
		//System.out.println(text);
        sent = sentClassifier.classify(text);
		if (sent.equalsIgnoreCase("Negative")) return 0;
		else if (sent.equalsIgnoreCase("Neutral")) return 1;
		else if (sent.equalsIgnoreCase("Positive")) return 2;
		return 1;
	}
	public static String getEntity(String text)
	{
		return DictionaryChunker.getEntity(DictionaryChunker.dictionaryChunkerTF,text);
		
	}
	public static String getEntitySubject(String text)
	{
		return DictionaryChunker.getEntityCategory(DictionaryChunker.dictionaryChunkerTF,text);

	}
	
}
