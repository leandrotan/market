package com.hdp.test;

import com.hdp.sshHelper.PatternValidator;
public class WildCardRegex {
    public WildCardRegex() {  }
    
    public static void main(String[] args) {
    	
    	String pattern = "([^\\s]+(\\.(?i)(stat|csv))$)";
		System.out.println(pattern);
		PatternValidator pv = new PatternValidator(pattern);
		String[] fileName={"ASSET_2G_SECTORS.stat","test.file_25.csv","sdp.04a_diameter.stat"};
		for(String file : fileName)
		{
			System.out.println(file);
			System.out.println(pv.validate(file));
		}
		
		/*
        String test = "ASSET_2G_SECTORS.stat";
        System.out.println(test);
        System.out.println(Pattern.matches(wildcardToRegex("ASSET*||2G_*||3G_*"), test));
        /*
        System.out.println(Pattern.matches(wildcardToRegex("?2*"), test));
        System.out.println(Pattern.matches(wildcardToRegex("??2*"), test));
        System.out.println(Pattern.matches(wildcardToRegex("*A*"), test));
        System.out.println(Pattern.matches(wildcardToRegex("*Z*"), test));
        System.out.println(Pattern.matches(wildcardToRegex("123*"), test));
        System.out.println(Pattern.matches(wildcardToRegex("123"), test));
        System.out.println(Pattern.matches(wildcardToRegex("*ABC"), test));
        System.out.println(Pattern.matches(wildcardToRegex("*abc"), test));
        System.out.println(Pattern.matches(wildcardToRegex("ABC*"), test));
        */
        /*
           output :
           123ABC
            true
            true
            false
            true
            false
            true
            false
            true
            false
            false
        */
        
    }
    
    public static String wildcardToRegex(String wildcard){
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch(c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                    // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return(s.toString());
    }
}

