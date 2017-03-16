package com.heroku.robo_adviser_sample.callback;

import java.io.IOException;

import com.google.inject.ImplementedBy;
import com.heroku.robo_adviser_sample.callback.impl.CallbackServiceImpl;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.CallbackRequest;

import spark.Request;

@ImplementedBy(CallbackServiceImpl.class)
public interface CallbackService {

	public CallbackRequest handle(Request req) throws CallbackException, IOException;
	
	public CallbackRequest handle(String signature, String payload) throws CallbackException, IOException;

	void pushMessage(PushMessage pushMessage);
	
	void replyMessage(ReplyMessage replyMessage);
	
}
