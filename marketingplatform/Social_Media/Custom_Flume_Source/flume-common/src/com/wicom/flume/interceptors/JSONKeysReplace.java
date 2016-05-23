package com.wicom.flume.interceptors;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONKeysReplace implements Interceptor {
	/**
	 * Interceptor class that replaces words inside event body (Should be JSON)
	 *
	 * Sample config:<p>
	 *
	 * <code>
	 *   agent.sources.FlumeLinkedInSource.interceptors = i1<p>
	 *   agent.sources.FlumeLinkedInSource.interceptors.i1.type = com.wicom.flume.interceptors.JSONKeysReplace$Builder<p>
	 *   agent.sources.FlumeLinkedInSource.interceptors.i1.map.oldWord = newWord<p>
	 *   agent.sources.FlumeLinkedInSource.interceptors.i1.map.oldWord2 = newWord2<p>
	 * </code>
	 *
	 */
	
	private static final Logger logger = LoggerFactory.getLogger(JSONKeysReplace.class);

	private Map<String, String> words;

	/**
	 * Only {@link JSONKeysReplace.Builder} can build me
	 */
	private JSONKeysReplace(Map<String, String> words) {
		this.words = words;
	}

	@Override
	public void initialize() {
		// no-op
	}

	/**
	 * Modifies events in-place.
	 */
	@Override
	public Event intercept(Event event) {
		logger.debug("Replace words in event body");
		try {
			//Get Json object string
			String eventStr = new String(event.getBody(),"utf-8");
			//Set json array string representation
			String eventStrArr = "["+eventStr.replace("\n", ",")+"]";
			if (isMultiNodesJSONArray(eventStrArr)) {
				logger.debug("Processing event JSON array");
				event.setBody(replaceKeys(new JSONArray(eventStrArr)).join("\n").getBytes());
			} else {
				logger.debug("Processing event JSON object");
				event.setBody(replaceKeys(new JSONObject(eventStr)).toString().getBytes());
			}
		} catch (Throwable t) {
			logger.error(ExceptionUtils.getStackTrace(t));
		}
		return event;
	}

	/**
	 * Delegates to {@link #intercept(Event)} in a loop.
	 * @param events
	 * @return
	 */
	@Override
	public List<Event> intercept(List<Event> events) {
		for (Event event : events) {
			intercept(event);
		}
    	return events;
	}

	@Override
	public void close() {
		// no-op
	}

	private JSONObject replaceKeys(JSONObject o) {
		Iterator<?> keys = o.keys();
		JSONObject newJSON = new JSONObject(o.toString());
		while(keys.hasNext()){
			String key = (String)keys.next();
			if (o.get(key) instanceof JSONObject ){
				JSONObject childObj = new JSONObject(o.getJSONObject(key).toString());
				newJSON.put(key, replaceKeys(childObj));
			} else if (o.get(key) instanceof JSONArray) {
				JSONArray childArr = new JSONArray(o.getJSONArray(key).toString());
				newJSON.put(key, replaceKeys(childArr));
			}
			if (words.containsKey(key)) {
				newJSON.put(words.get(key), newJSON.get(key));
				newJSON.remove(key);
			}
		}
		return newJSON;
	}
	
	private JSONArray replaceKeys(JSONArray o) {
		JSONArray newArray = new JSONArray(o.toString());
		for (int i=0;i<o.length();i++) {
			if (o.get(i) instanceof JSONObject) {
				JSONObject chl = new JSONObject(o.getJSONObject(i).toString());
				newArray.put(i, replaceKeys(chl));
			} else if (o.get(i) instanceof JSONArray) {
				return replaceKeys(o.getJSONArray(i));
			}
		}
		return newArray;
	}
	
	private boolean isMultiNodesJSONArray(String jsonStrArr) {
		try {
			JSONArray a = new JSONArray(jsonStrArr);
			if (a.length()>1) {
				return true;
			}
			return false;
		} catch (JSONException e) {
			return false;
		}
	}
	  
	/**
	* Builder which builds new instances of the JSONKeysReplace interceptor.
   	*/
	public static class Builder implements Interceptor.Builder {
	    private Map<String, String> words;
	    @Override
	    public Interceptor build() {
	    	return new JSONKeysReplace(words);
	    }
	    @Override
	    public void configure(Context context) {
	    	words = context.getSubProperties("map.");
	    }
	}
}
