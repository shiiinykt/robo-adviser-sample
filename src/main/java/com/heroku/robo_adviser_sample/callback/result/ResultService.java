package com.heroku.robo_adviser_sample.callback.result;

import java.util.List;

import com.google.inject.ImplementedBy;
import com.heroku.robo_adviser_sample.callback.UserAnswer;
import com.heroku.robo_adviser_sample.callback.result.impl.ResultServiceImpl;

@ImplementedBy(ResultServiceImpl.class)
public interface ResultService {
	
	List<Result> loadAll();
	
	Result load(String id);

	Result calc(List<UserAnswer> userAnswers);
}
