package com.heroku.robo_adviser_sample.callback;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class UserAnser {

	private String questionId;
	private Integer anserId;
	
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static UserAnser getObj(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, UserAnser.class);
	}
}
