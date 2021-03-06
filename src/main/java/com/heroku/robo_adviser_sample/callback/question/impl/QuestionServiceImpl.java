package com.heroku.robo_adviser_sample.callback.question.impl;

import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.heroku.robo_adviser_sample.callback.question.Question;
import com.heroku.robo_adviser_sample.callback.question.QuestionService;

public class QuestionServiceImpl implements QuestionService {

	@SuppressWarnings("unchecked")
	@Override
	public List<Question> loadAll() {
		Yaml yaml = new Yaml();
		return (List<Question>) yaml.load(ClassLoader.getSystemResourceAsStream("yaml/questions.yml"));
	}

	@Override
	public Question load(String id) {
		return loadAll().stream().filter(q -> id.equals(q.getId())).findFirst().orElse(null);
	}

}
