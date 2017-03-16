package com.heroku.robo_adviser_sample.callback.question;

import java.util.List;

import lombok.Data;

@Data
public class Question {
	private Integer no;
	private String question;
	private List<String> ansers;
}
