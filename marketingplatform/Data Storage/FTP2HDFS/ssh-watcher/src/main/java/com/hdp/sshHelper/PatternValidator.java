package com.hdp.sshHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternValidator {
	
		private Pattern pattern;
		private Matcher matcher;
		
		public PatternValidator(String file_pattern){
			pattern=Pattern.compile(file_pattern);
		}
		
		public boolean validate(String fileName){
			matcher=pattern.matcher(fileName);
			return matcher.matches();
		}

}
