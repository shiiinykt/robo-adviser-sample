package com.heroku.robo_adviser_sample.callback;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class UserAnswer {

	private String questionId;
	private Integer answerId;
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static UserAnswer getObj(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, UserAnswer.class);
	}
}
