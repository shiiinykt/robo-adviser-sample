package com.heroku.robo_adviser_sample.callback;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.heroku.robo_adviser_sample.callback.question.Question;
import com.heroku.robo_adviser_sample.callback.question.QuestionService;
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
	
	private static String ACTION_ID = "aid";
	private static String QUESTION_ID = "qid";
	
	private static String BEGIN = "BEGIN";
	private static String PROCESS = "PROCESS";
	private static String END = "END";
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
		if (LineSession.get(event) == null) {
			initial(event);
		} else if (BEGIN.equals(LineSession.get(event).attribute(ACTION_ID))) {
			begin(event);
		} else if (PROCESS.equals(LineSession.get(event).attribute(ACTION_ID))) {
			process(event); 
		} else if (END.equals(LineSession.get(event).attribute(ACTION_ID))) {
			end(event); 
		}
	}
	
	private static void initial(Event event) {
		LineSession session = LineSession.get(event, true);
		session.attribute(ACTION_ID, BEGIN);

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
				LineSession session = LineSession.get(event);
				
			if (YES.equals(((TextMessageContent)((MessageEvent<?>) event).getMessage()).getText())) {
				session.attribute(ACTION_ID, PROCESS);
				session.attribute(QUESTION_ID, 1); 
				handleQuestion(event);

			} else if(NO.equals(((TextMessageContent)((MessageEvent<?>) event).getMessage()).getText())) {
				session.invalidate();

			}
		}
	}
	
	private static void process(Event event) {
		LineSession session = LineSession.get(event);
		int no = session.attribute(QUESTION_ID);
		
		if (no == 1 || event instanceof PostbackEvent) {
			
			if (no <= questionService.loadAll().size()) {
				Question question = questionService.load(no);
				List<Action> ansers = new ArrayList<Action>();
				
				for (int i = 0; i < question.getAnsers().size(); i ++) {
					ansers.add(new PostbackAction(question.getAnsers().get(i), String.valueOf(i)));
				}
				
				TemplateMessage templateMessage = new TemplateMessage("質問" + no,new ButtonsTemplate(null, "質問" + no, question.getQuestion(), ansers));
				PushMessage pushMessage = new PushMessage(event.getSource().getUserId(), templateMessage);
				service.pushMessage(pushMessage);
				
				session.attribute(QUESTION_ID, (Integer) session.attribute(QUESTION_ID) + 1);
			} else {
				session.attribute(ACTION_ID, END);
				handleQuestion(event);
			}
		}
	}
	
	private static void end(Event event) {
		TextMessage text = new TextMessage("診断が終わりました。");
		PushMessage message = new PushMessage(event.getSource().getUserId(), text);
		service.pushMessage(message);
		
		LineSession session = LineSession.get(event);
		session.invalidate();
	}
}
