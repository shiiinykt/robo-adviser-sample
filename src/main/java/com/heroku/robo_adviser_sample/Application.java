package com.heroku.robo_adviser_sample;

import static spark.Spark.port;
import static spark.Spark.post;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.heroku.robo_adviser_sample.callback.CallbackController;


public class Application {
	
	public static void main(String[] args) {
		new Application().init();
	}
	
	private void init() {
		port(Integer.valueOf(System.getenv("PORT")));
		inject();
		
		post("/callback", CallbackController.callback);
	}
	
	private static void inject() {
		Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				requestStaticInjection(CallbackController.class);
			}
		});
	}
	
}
