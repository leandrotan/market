package com.wicom.flume.sources.twitter;

import static org.apache.flume.interceptor.TimestampInterceptor.Constants.TIMESTAMP;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wicom.flume.sources.twitter.feeds.StatusStreamJSON;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class UserStatusStream extends AbstractSource
    						   implements EventDrivenSource, Configurable {
	
	private static final Logger logger = LoggerFactory.getLogger(UserStatusStream.class);
	
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	private String selectorHeader;
	private String[] keywords;
  	private TwitterStream twitterStream;

	public void configure(Context context) {
		consumerKey = context.getString("consumerKey");
	    consumerSecret = context.getString("consumerSecret");
	    accessToken = context.getString("accessToken");
	    accessTokenSecret = context.getString("accessTokenSecret");
	    selectorHeader = context.getString("selector.header");

	    String keywordString = context.getString("keywords", "");
	    if (keywordString.trim().length() == 0) {
	        keywords = new String[0];
	    } else {
	      keywords = keywordString.split(",");
	      for (int i = 0; i < keywords.length; i++) {
	        keywords[i] = keywords[i].trim();
	      }
	    }
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setOAuthConsumerKey(consumerKey);
	    cb.setOAuthConsumerSecret(consumerSecret);
	    cb.setOAuthAccessToken(accessToken);
	    cb.setOAuthAccessTokenSecret(accessTokenSecret);
	    cb.setJSONStoreEnabled(true);
	    cb.setIncludeEntitiesEnabled(true);
	    cb.setUserStreamRepliesAllEnabled(true);
	    twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	}

	@Override
	public void start() {
		UserStreamListener listener = getStreamListener();
		twitterStream.addListener(listener);
		if (keywords.length == 0) {
			logger.info("Starting up Twitter User status...");
			twitterStream.user();
		} else {
			logger.info("Starting up Twitter User status filtering...");
			twitterStream.user(keywords);
		}
		super.start();
	}

	@Override
	public void stop() {
		logger.info("Shutting down UserStatus stream...");
		twitterStream.shutdown();
		super.stop();
	}
	
	private UserStreamListener getStreamListener() {
		final ChannelProcessor channel = getChannelProcessor();
		final Map<String, String> headers = new HashMap<String, String>();
		return new UserStreamListener() {
			// The onStatus method is executed every time a new tweet comes in.
			public void onStatus(Status status) {
				headers.put(TIMESTAMP, Long.toString(status.getCreatedAt().getTime()));
				if (StatusStreamJSON.isNormal(DataObjectFactory.getRawJSON(status))) {
					headers.put(selectorHeader, "NORMAL");
				} else {
					logger.error("Add error message header");
					headers.put(selectorHeader, "ERROR");
				}
				StatusStreamJSON userStreamJSON = new StatusStreamJSON(status);
				userStreamJSON.setMatchedKeywordsTag(keywords);
				Event event = EventBuilder.withBody(userStreamJSON.toString().getBytes(), headers);
				channel.processEvent(event);
			}
			
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				logger.debug("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}
			
			public void onDeletionNotice(long directMessageId, long userId) {
				logger.debug("Got a direct message deletion notice id:" + directMessageId);
			}
			
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				logger.debug("Got a track limitation notice:" + numberOfLimitedStatuses);
			}
			
			public void onScrubGeo(long userId, long upToStatusId) {
				logger.debug("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}
			
			public void onStallWarning(StallWarning warning) {
				logger.debug("Got stall warning:" + warning);
			}
			
			public void onFriendList(long[] friendIds) {
				JSONObject friendsEvent = new JSONObject();
				JSONArray arr = new JSONArray(friendIds);
				final Map<String, String> headers = new HashMap<String, String>();
			    headers.put(TIMESTAMP, Long.toString(System.currentTimeMillis()));
				friendsEvent.put("friendsList", arr);
				Event event = EventBuilder.withBody(friendsEvent.toString().getBytes(), headers);
				channel.processEvent(event);
			}
			
			public void onFavorite(User source, User target, Status favoritedStatus) {
				logger.debug("onFavorite source:@"
				+ source.getScreenName() + " target:@"
				+ target.getScreenName() + " @"
				+ favoritedStatus.getUser().getScreenName() + " - "
				+ favoritedStatus.getText());
			}
			
			public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
				logger.debug("onUnFavorite source:@"
				+ source.getScreenName() + " target:@"
				+ target.getScreenName() + " @"
				+ unfavoritedStatus.getUser().getScreenName()
				+ " - " + unfavoritedStatus.getText());
			}
			
			public void onFollow(User source, User followedUser) {
				logger.debug("onFollow source:@"
				+ source.getScreenName() + " target:@"
				+ followedUser.getScreenName());
			}

			public void onDirectMessage(DirectMessage directMessage) {
				logger.debug("onDirectMessage text:"
						+ directMessage.getText());
			}

			public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
				logger.debug("onUserListMemberAddition added member:@"
				+ addedMember.getScreenName()
				+ " listOwner:@" + listOwner.getScreenName()
				+ " list:" + list.getName());
			}
			
			public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
				logger.debug("onUserListMemberDeleted deleted member:@"
				+ deletedMember.getScreenName()
				+ " listOwner:@" + listOwner.getScreenName()
				+ " list:" + list.getName());
			}
			
			public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
				logger.debug("onUserListSubscribed subscriber:@"
				+ subscriber.getScreenName()
				+ " listOwner:@" + listOwner.getScreenName()
				+ " list:" + list.getName());
			}
			
			public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
				logger.debug("onUserListUnsubscribed subscriber:@"
				+ subscriber.getScreenName()
				+ " listOwner:@" + listOwner.getScreenName()
				+ " list:" + list.getName());
			}
			
			public void onUserListCreation(User listOwner, UserList list) {
				logger.debug("onUserListCreated listOwner:@"
				+ listOwner.getScreenName()
				+ " list:" + list.getName());
			}
			
			public void onUserListUpdate(User listOwner, UserList list) {
				logger.debug("onUserListUpdated listOwner:@"
				+ listOwner.getScreenName()
				+ " list:" + list.getName());
			}
			
			public void onUserListDeletion(User listOwner, UserList list) {
				logger.debug("onUserListDestroyed listOwner:@"
				+ listOwner.getScreenName()
				+ " list:" + list.getName());
			}
			
			public void onUserProfileUpdate(User updatedUser) {
				logger.debug("onUserProfileUpdated user:@" + updatedUser.getScreenName());
			}
			
			public void onBlock(User source, User blockedUser) {
				logger.debug("onBlock source:@" + source.getScreenName()
						+ " target:@" + blockedUser.getScreenName());
			}
			
			public void onUnblock(User source, User unblockedUser) {
				logger.debug("onUnblock source:@" + source.getScreenName()
						+ " target:@" + unblockedUser.getScreenName());
			}
			
			public void onException(Exception ex) {
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		};
	}
}
