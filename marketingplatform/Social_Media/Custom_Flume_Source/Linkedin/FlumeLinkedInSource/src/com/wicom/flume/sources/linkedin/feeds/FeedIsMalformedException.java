package com.wicom.flume.sources.linkedin.feeds;

public class FeedIsMalformedException extends Exception {
	private static final long serialVersionUID = 2L;
	private String response;
	public FeedIsMalformedException(String rawResponse) {
		super("UpdateFeed is malformed. Use raw update response for debug information");
		response = rawResponse;
	}
	
	public String getRawResponse() {
		return response;
	}
}
