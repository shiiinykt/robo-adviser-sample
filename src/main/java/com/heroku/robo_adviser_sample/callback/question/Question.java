package com.heroku.robo_adviser_sample.callback.question;

import java.util.List;

import lombok.Data;

@Data
public class Question {
	private String id;
	private String question;
	private String imageUrl;
	private List<String> answers;
}
