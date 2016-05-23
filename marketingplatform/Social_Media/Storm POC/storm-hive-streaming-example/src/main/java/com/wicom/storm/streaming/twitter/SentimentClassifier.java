package com.wicom.storm.streaming.twitter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.stats.MultivariateDistribution;
import com.aliasi.util.AbstractExternalizable;
import org.apache.log4j.lf5.util.ResourceUtils;


public class SentimentClassifier {

	String[] categories;
	LMClassifier classLM;

	public SentimentClassifier() {
        File f = new File("src/main/resources/classifier.txt");

try {

    classLM= (LMClassifier) AbstractExternalizable.readObject(f.getCanonicalFile());
        categories = classLM.categories();
       // System.out.println("FIle: "+new File(classLoader.getResource("classifier.txt").getFile()));
	}
	catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
	catch (IOException e) {
		e.printStackTrace();
	}

	}

	public String classify(String text) {
	ConditionalClassification classification = classLM.classify(text);
	return classification.bestCategory();
	}
	
	
	
}
