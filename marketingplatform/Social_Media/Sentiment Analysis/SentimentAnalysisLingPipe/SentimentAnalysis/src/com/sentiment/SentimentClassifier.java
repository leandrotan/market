package com.sentiment;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.stats.MultivariateDistribution;
import com.aliasi.util.AbstractExternalizable;
import com.google.common.base.Charsets;


public class SentimentClassifier {

	String[] categories;
	LMClassifier classLM;

	public SentimentClassifier() {
	
	try {
		classLM= (LMClassifier) AbstractExternalizable.readObject(new File("classifier.txt"));
		categories = classLM.categories();
	}
	catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
	catch (IOException e) {
		e.printStackTrace();
	}

	}

	public String classify(String text) {
		Charset charset = Charset.forName("UTF-8");
		byte[] stringBytes = text.getBytes(charset);
		String convertedStr = new String(stringBytes, Charsets.UTF_8);
	ConditionalClassification classification = classLM.classify(convertedStr);
	return classification.bestCategory();
	
	}
	
	
	
}
