package com.heroku.robo_adviser_sample.callback.result.impl;

import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.heroku.robo_adviser_sample.callback.UserAnswer;
import com.heroku.robo_adviser_sample.callback.result.Result;
import com.heroku.robo_adviser_sample.callback.result.ResultService;

public class ResultServiceImpl implements ResultService {

	@SuppressWarnings("unchecked")
	@Override
	public List<Result> loadAll() {
		Yaml yaml = new Yaml();
		return (List<Result>) yaml.load(ClassLoader.getSystemResourceAsStream("yaml/results.yml"));
	}

	@Override
	public Result load(String id) {
		return loadAll().stream().filter(r -> id.equals(r.getId())).findFirst().orElse(null);
	}

	@Override
	public Result calc(List<UserAnswer> userAnswers) {
		int num = 0;
		
		for (UserAnswer userAnswer : userAnswers) {
			num += userAnswer.getAnswerId();
		}
		
		return loadAll().get(num % loadAll().size());
	}

}
