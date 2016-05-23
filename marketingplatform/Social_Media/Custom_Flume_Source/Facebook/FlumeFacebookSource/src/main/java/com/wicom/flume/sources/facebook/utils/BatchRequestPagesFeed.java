package com.wicom.flume.sources.facebook.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.sources.facebook.feeds.PageFeedJSON;


/**
 * @BatchRequestPagesFeed Provides URL for batch request
 *
 */
public class BatchRequestPagesFeed {
	
	private static final Logger logger = LoggerFactory
			.getLogger(BatchRequestPagesFeed.class);
	
	private JSONArray requestVals;
	private ArrayList<String> pagesList;
	private final String FEED_FIELDS = "id,from,message,picture,link,privacy,type,status_type,object_id,created_time,updated_time,shares,is_hidden,comments.limit(250).filter(stream){id,message,from,user_likes,message_tags,like_count,created_time,parent{id}},likes,caption,story,properties"; 
	
	public BatchRequestPagesFeed(String [] pageIds, long since, int posts_limit) {
		requestVals = new JSONArray();
		pagesList = new ArrayList<String>();
		for (String pageId: pageIds) {
			JSONObject requestItem = new JSONObject();
			requestItem.put("method", "GET");
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(pageId)
					.append("/feed?limit="+posts_limit)
					.append("&since=").append(since)
					.append("&fields=").append(FEED_FIELDS);
			try {
				requestItem.put("relative_url", URLEncoder.encode(urlBuilder.toString(),StandardCharsets.UTF_8.name()));
			} catch (UnsupportedEncodingException e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
			requestVals.put(requestItem);
			pagesList.add(pageId);
		}
	}
	
	public BatchRequestPagesFeed (ArrayList<PageFeedJSON> pages, long since) {
		requestVals = new JSONArray();
		pagesList = new ArrayList<String>();
		for (PageFeedJSON page: pages) {
			if ((!page.isError())&&page.hasNext()) {
				JSONObject requestItem = new JSONObject();
				requestItem.put("method", "GET");
				String nextUrl = page.getNextURL();
				pagesList.add(nextUrl.substring(0, nextUrl.indexOf("/")));
				logger.debug("Batch request generated for: "+nextUrl.substring(0, nextUrl.indexOf("/")));
				try {
					requestItem.put("relative_url", URLEncoder.encode(nextUrl+"&since="+since,StandardCharsets.UTF_8.name()));
				} catch (UnsupportedEncodingException e) {
					logger.error(ExceptionUtils.getStackTrace(e));
				}
				requestVals.put(requestItem);
			}
		}
	}
	
	public String generateBatchRequest() {
		return requestVals.toString();
	}
	
	public ArrayList<String> getPageIds() {
		return pagesList;
	}
}
