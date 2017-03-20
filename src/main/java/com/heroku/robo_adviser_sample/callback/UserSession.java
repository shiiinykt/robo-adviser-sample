package com.heroku.robo_adviser_sample.callback;

import java.util.ArrayList;
import java.util.List;

import com.heroku.robo_adviser_sample.callback.question.Question;
import com.linecorp.bot.model.event.Event;

public class UserSession extends LineSession {

	public static String ACTION_ID = UserSession.class.getName() + ".ACTION_ID";
	public static String USER_ANSERS = UserSession.class.getName() + ".USER_ANSERS";
	
	public static String BEGIN = UserSession.class.getName() + ".BEGIN";
	public static String PROCESS = UserSession.class.getName() + ".PROCESS";
	public static String END = UserSession.class.getName() + ".END";
	
	
	protected UserSession(String userId) {
		super(userId);	
	}
		

	public static UserSession get(Event event) {
		return get(event, false);
	}
	
	public static UserSession get(Event event, boolean created) {
		String userId = event.getSource().getUserId();
		
		if(created && !lineSessionStore.containsKey(userId)) {
			lineSessionStore.put(userId, new UserSession(userId));
		}
		
		return (UserSession) lineSessionStore.get(userId);
	}	
	public String getAction() {
		return attribute(ACTION_ID);
	}
	
	public void setAction(String action) {
		attribute(ACTION_ID, action);
	}
	
	public void addUserAnser(UserAnser userAnser) {
		List<UserAnser>userAnsers = getUserAnsers();
		
		userAnsers.add(userAnser);
	}
	
	public UserAnser getUserAnser(String id) {
		List<UserAnser> userAnsers = getUserAnsers();
		
		return userAnsers.stream().filter(u -> id.equals(u.getQuestionId())).findFirst().orElseGet(null);
	}
	
	public List<UserAnser> getUserAnsers() {
		if (attribute(USER_ANSERS) == null) {
			attribute(USER_ANSERS, new ArrayList<UserAnser>());
		}
		
		List<UserAnser> userAnsers = attribute(USER_ANSERS);
		return userAnsers;
	}
	
	public Question getUnAnsweredQuestion(List<Question> list) {
		
		for (Question q : list) {
			if (getUserAnsers().stream().filter(ua -> q.getId().equals(ua.getQuestionId())).count() == 0) {
				return q;
			}
		}
		
		return null;
	}
}
