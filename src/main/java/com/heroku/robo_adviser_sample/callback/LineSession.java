package com.heroku.robo_adviser_sample.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import com.linecorp.bot.model.event.Event;

public class LineSession {
	private static Cache<String, LineSession> lineSessionStore = new Cache2kBuilder<String, LineSession>() {}
	.expireAfterWrite(5, TimeUnit.MINUTES)
	.build();

	private Map<String, Object> attributes;
	private String userId;
	
	private LineSession(String userId) {
		attributes = new HashMap<String, Object>();
		this.userId = userId;
	}
	
	public static LineSession get(Event event) {
		return get(event, false);
	}
	
	public static LineSession get(Event event, boolean created) {
		String userId = event.getSource().getUserId();
		
		if(created && !lineSessionStore.containsKey(userId)) {
			lineSessionStore.put(userId, new LineSession(userId));
		}
		
		
		return lineSessionStore.get(userId);
	}
	
	public void invalidate() {
		lineSessionStore.remove(userId);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T attribute(String key) {
		return (T) attributes.get(key);
	}
	
	public void attribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	public void removeAttribute(String key) {
		attributes.remove(key);
	}
}
