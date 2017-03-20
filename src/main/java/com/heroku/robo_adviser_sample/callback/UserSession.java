package com.heroku.robo_adviser_sample.callback;

import java.util.ArrayList;
import java.util.List;

import com.heroku.robo_adviser_sample.callback.question.Question;
import com.linecorp.bot.model.event.Event;

public class UserSession extends LineSession {

	public static String ACTION_ID = UserSession.class.getName() + ".ACTION_ID";
	public static String USER_ANSWERS = UserSession.class.getName() + ".USER_ANSWERS";
	
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
	
	public void addUserAnswer(UserAnswer userAnswer) {
		List<UserAnswer>userAnswers = getUserAnswers();
		
		userAnswers.add(userAnswer);
	}
	
	public UserAnswer getUserAnswer(String id) {
		List<UserAnswer> userAnswers = getUserAnswers();
		
		return userAnswers.stream().filter(u -> id.equals(u.getQuestionId())).findFirst().orElseGet(null);
	}
	
	public List<UserAnswer> getUserAnswers() {
		if (attribute(USER_ANSWERS) == null) {
			attribute(USER_ANSWERS, new ArrayList<UserAnswer>());
		}
		
		List<UserAnswer> userAnswers = attribute(USER_ANSWERS);
		return userAnswers;
	}
	
	public Question getUnAnsweredQuestion(List<Question> list) {
		
		for (Question q : list) {
			if (getUserAnswers().stream().filter(ua -> q.getId().equals(ua.getQuestionId())).count() == 0) {
				return q;
			}
		}
		
		return null;
	}
}
