package com.heroku.robo_adviser_sample.callback.question;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.heroku.robo_adviser_sample.callback.question.impl.QuestionServiceImpl;

@ImplementedBy(QuestionServiceImpl.class)
public interface QuestionService {
	
	List<Question> loadAll();
	
	Question load(String id);

}
