package com.heroku.robo_adviser_sample.callback;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.heroku.robo_adviser_sample.callback.question.Question;
import com.heroku.robo_adviser_sample.callback.question.QuestionService;
import com.heroku.robo_adviser_sample.callback.result.Result;
import com.heroku.robo_adviser_sample.callback.result.ResultService;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.CallbackRequest;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;

import spark.Request;
import spark.Response;
import spark.Route;

public class CallbackController {
	
	@Inject
	private static CallbackService service;
	@Inject
	private static QuestionService questionService;
	@Inject
	private static ResultService resultService;
	
	private static String YES = "はい";
	private static String NO = "いいえ";
	
	public static Route callback = (Request req, Response res) -> {
		CallbackRequest request = service.handle(req);
		
		request.getEvents().stream().forEach(event -> {
			handleQuestion(event);
		});
		
		return "OK";
	};
	
	private static void handleQuestion(Event event) {
		UserSession session = UserSession.get(event);
		
		if (session == null) {
			initial(event);
		} else if (UserSession.BEGIN.equals(session.getAction())) {
			begin(event);
		} else if (UserSession.PROCESS.equals(session.getAction())) {
			process(event); 
		} else if (UserSession.END.equals(session.getAction())) {
			end(event); 
		}
	}
	
	private static void initial(Event event) {
		UserSession session = (UserSession) UserSession.get(event, true);
		session.setAction(UserSession.BEGIN);

		List<Action> actions = new ArrayList<Action>();
		actions.add(new MessageAction("はい", YES));
		actions.add(new MessageAction("いいえ", NO));
		
		TemplateMessage templateMessage = new TemplateMessage("診断開始",
			new ConfirmTemplate("診断を開始しますか。", actions));
		PushMessage pushMessage = new PushMessage(event.getSource().getUserId(), templateMessage);
		service.pushMessage(pushMessage);
	}

	private static void begin(Event event) {
		if (event instanceof MessageEvent<?>
			&& ((MessageEvent<?>) event).getMessage() instanceof TextMessageContent) {
			UserSession session = UserSession.get(event);
			
			if (YES.equals(((TextMessageContent)((MessageEvent<?>) event).getMessage()).getText())) {
				session.setAction(UserSession.PROCESS);;
				handleQuestion(event);

			} else if(NO.equals(((TextMessageContent)((MessageEvent<?>) event).getMessage()).getText())) {
				session.invalidate();

			}
		}
	}
	
	private static void process(Event event) {
		UserSession session = UserSession.get(event, true);
		
		if (session.getUserAnswers().size() == 0 || event instanceof PostbackEvent) {
			
			if (event instanceof PostbackEvent) {
				UserAnswer userAnswer = UserAnswer.getObj(((PostbackEvent) event).getPostbackContent().getData());
				session.addUserAnswer(userAnswer);
			}
			
			if (session.getUnAnsweredQuestion(questionService.loadAll()) != null) {
				Question question = session.getUnAnsweredQuestion(questionService.loadAll());
				List<Action> answers = new ArrayList<Action>();
				
				for (int i = 0; i < question.getAnswers().size(); i ++) {
					UserAnswer userAnswer = new UserAnswer();
					userAnswer.setQuestionId(question.getId());
					userAnswer.setAnswerId(i);
					
					answers.add(new PostbackAction(question.getAnswers().get(i), userAnswer.toJson()));
				}
				
				TemplateMessage templateMessage = new TemplateMessage(question.getId(), new ButtonsTemplate(question.getImageUrl(), question.getId(), question.getQuestion(), answers));
				PushMessage pushMessage = new PushMessage(event.getSource().getUserId(), templateMessage);
				service.pushMessage(pushMessage);
				
			} else {
				session.setAction(UserSession.END);
				handleQuestion(event);
			}
		}
	}
	
	private static void end(Event event) {
		TextMessage text = new TextMessage("診断が終わりました。");
		PushMessage message = new PushMessage(event.getSource().getUserId(), text);
		service.pushMessage(message);
		
		UserSession session = UserSession.get(event);
		Result result = resultService.calc(session.getUserAnswers());
		
		text = new TextMessage(result.getResult());
		message = new PushMessage(event.getSource().getUserId(), text);
		service.pushMessage(message);
		
		session.invalidate();
	}
}
