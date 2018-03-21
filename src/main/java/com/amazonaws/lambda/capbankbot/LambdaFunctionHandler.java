package com.amazonaws.lambda.capbankbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Map<String, Object>, Object> {

	String formUrl = "https://www.google.com";

	@Override
	public Object handleRequest(Map<String, Object> input, Context context) {
		int i = 0;
		String userId = (String) input.get("userId");
		
	    System.out.println("userId -- " + userId);
		Map<String, Object> currentIntent = (Map<String, Object>) input.get("currentIntent");
		String slotString = currentIntent.get("slots").toString();
		LexRequest lexRequest = (LexRequest) LexRequestFactory.createLexRequest(input);
		DialogAction dialogAction = new DialogAction("Close", "Fulfilled",
				new Message("PlainText", "Sorry I did not understand what you said. You have a nice day!"));
		ResponseCard responseCard;
		try {
			String responseToLexMsg = slotString;
			i = i + 1;
			// thanksIntent
			int equalIndex6 = responseToLexMsg.indexOf("thanks=") + 7;
			i = i + 1;
			String responseToLexMsg7 = responseToLexMsg.substring(equalIndex6);
			i = i + 1;
			int commaIndex5 = responseToLexMsg7.indexOf(",");
			String thanks = responseToLexMsg7.substring(0, commaIndex5);
			i = i + 1;
			if (!(thanks.equalsIgnoreCase("null"))) {
				Message message = new Message("PlainText", "You are welcome, am glad I was able to assist. For more information, please visit https://www.cap-bank.us. We are here to help if you need support");
				dialogAction = new DialogAction("Close", "Fulfilled", message);
				i = i + 1;
			} else {
				// filltypeintent
				int equalIndex5 = responseToLexMsg.indexOf("filltypeintent=") + 15;
				i = i + 1;
				String responseToLexMsg6 = responseToLexMsg.substring(equalIndex5);
				i = i + 1;
				int commaIndex4 = responseToLexMsg6.indexOf(",");
				String filltypeintent = responseToLexMsg6.substring(0, commaIndex4);
				i = i + 1;
				if (!(filltypeintent.equalsIgnoreCase("null"))) {
					String defaultLink = "http://54.195.246.137/services/JsonResponceServlet?fName=karthik&sName=bajjuri&email=abc@gm.co&phone=12345678&preference=loan";
					LambdaLogger logger = context.getLogger();
					Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
					String formLinkMessage = "Here is the link to a partially filled application for you – " + formUrl;
					dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots, "thanks",
							new Message("PlainText", formLinkMessage));
					i = i + 1;
				} else {
					// usercardintent
					int equalIndex4 = responseToLexMsg.indexOf("usercardintent=") + 15;
					i = i + 1;
					String responseToLexMsg5 = responseToLexMsg.substring(equalIndex4);
					i = i + 1;
					int commaIndex3 = responseToLexMsg5.indexOf(",");
					String usercardintent = responseToLexMsg5.substring(0, commaIndex3);
					i = i + 1;
					if (!(usercardintent.equalsIgnoreCase("null"))) {
						AEMService aemService = new AEMService();
						formUrl = aemService.getCardRegisterFormLink("ravi", "kakran", "12345678", "abc@cap.com",
								usercardintent);
						LambdaLogger logger = context.getLogger();
						Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
						String fillIntentMessage = "If you would like to complete your application now, "
								+ "I can assist you with that or you can complete the application yourself online."
								+ " Please suggest.";
						dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots, "filltypeintent",
								new Message("PlainText", fillIntentMessage));
						i = i + 1;
					} else {
						// featureIntent
						int equalIndex3 = responseToLexMsg.indexOf("featureintent=") + 14;
						i = i + 1;
						String responseToLexMsg4 = responseToLexMsg.substring(equalIndex3);
						i = i + 1;
						int endBrcktIndex2 = responseToLexMsg4.indexOf("}");
						String featureIntent = responseToLexMsg4.substring(0, endBrcktIndex2);
						i = i + 1;
						
						if (!(featureIntent.equalsIgnoreCase("null"))) {
							LambdaLogger logger = context.getLogger();
							
							//Service
							AEMService aemService = new AEMService();
							JSONArray creditcardOffers = aemService.getCreditCardOffers();
							String elementString = "";
							ArrayList<Object> cardList = new ArrayList<Object>();
							Card card;
							for (int j = 0, size = creditcardOffers.length(); j < size; j++)
						    {
						      JSONObject objectInArray = creditcardOffers.getJSONObject(j);
						      String[] elementNames = JSONObject.getNames(objectInArray);
						      System.out.printf("%d ELEMENTS IN CURRENT OBJECT:\n", elementNames.length);
						      card = new Card(objectInArray.getString("product_name"), objectInArray.getString("thumbnail_path"), objectInArray.getString("page_link"));
						      //card = new Card(objectInArray.getString("product_name"), objectInArray.getString("thumbnail_path"),"www.cap-bank.us");
						      cardList.add(card);
						      System.out.println();
						    }
							responseCard = processCardListResponse(cardList);
							//Dummy
							//responseCard = processResponse();
							
							
							Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
							dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots, "usercardintent", responseCard, new Message("PlainText",
											"Here are some good offers for you - What type of card would you like to choose?"));
							i = i + 1;
						} else {
							// Score
							int equalIndex2 = responseToLexMsg.indexOf("userscore=") + 10;
							String responseToLexMsg3 = responseToLexMsg.substring(equalIndex2);
							int commaIndex2 = responseToLexMsg3.indexOf(",");
							String userscore = responseToLexMsg3.substring(0, commaIndex2);
							i = i + 1;
							if (!(userscore.equalsIgnoreCase("null"))) {
								if (userscore.equalsIgnoreCase("500") || userscore.equalsIgnoreCase("800")
										|| userscore.equalsIgnoreCase("500 - 800")) {
									Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
									String userMsg = "Thanks ;  you have a good credit score, keep it up. Are you looking for any specific features like Travel miles, hotel miles, cash rewards?";
									if(userscore.equalsIgnoreCase("500")){
										userMsg = "Thanks ; Are you looking for any specific features like Travel miles, hotel miles, cash rewards?";
									}
									dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots,
											"featureintent", new Message("PlainText",
													userMsg));

								} else {
									dialogAction = new DialogAction("Close", "Fulfilled", new Message("PlainText",
											"Sorry, I did not get your score right; Please try again from beginning and select an option. You have a nice day!"));
								}
							} else {
								// Agrre to Answer
								int equalIndex = responseToLexMsg.indexOf("agreetoanswer=") + 14;
								String responseToLexMsg2 = responseToLexMsg.substring(equalIndex);
								int commaIndex = responseToLexMsg2.indexOf(",");
								String agreeToAnswer = responseToLexMsg2.substring(0, commaIndex);
								if (!(agreeToAnswer.equalsIgnoreCase("null"))) {
									if (agreeToAnswer.equalsIgnoreCase("no")) {
										dialogAction = new DialogAction("Close", "Fulfilled",
												new Message("PlainText", "Okay. You have a nice day!"));
									} else {
										responseCard = processScoreResponsecard();
										Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
										dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots,
												"userscore", responseCard);
									}
								} else {
									int equalIndex0 = responseToLexMsg.indexOf("creditcard=") + 11;
									String responseToLexMsg0 = responseToLexMsg.substring(equalIndex0);
									int commaIndex0 = responseToLexMsg0.indexOf(",");
									String creditcardintent = responseToLexMsg0.substring(0, commaIndex0);
									if (creditcardintent.toLowerCase().indexOf("credit") != -1) {
										Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
										dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots,
												"agreetoanswer");
									} else {
										dialogAction = new DialogAction("Close", "Fulfilled", new Message("PlainText",
												"Sorry ; I did not understand. This service is only for applying new credit card. Please try again from beginning. You have a nice day!"));
									}
								}
							}
						}
					}
				}
			}
			return new LexResponse(dialogAction);
		} catch (Exception e) {
			dialogAction = new DialogAction("Close", "Fulfilled",
					new Message("PlainText", "Sorry! I did not understand; For more information, please visit https://www.cap-bank.us. We are here to help if you need support"));
			return new LexResponse(dialogAction);
		}

	}

	private ResponseCard processScoreResponsecard() {
		// String scoreString = "<500 | 500 to 600 | 600 to 700 | 700 to 800 | 800+";
		Button buttonArray[] = new Button[3];
		Button button = null;
		button = new Button("500 or less", "500");
		buttonArray[0] = button;
		button = new Button("500 - 800", "500 - 800");
		buttonArray[1] = button;
		button = new Button("800 or more", "800");
		buttonArray[2] = button;
		Attachment attachmentArray[] = new Attachment[1];
		attachmentArray[0] = new Attachment(buttonArray, "What is your approximate credit score?", "Select the Score");
		ResponseCard responseCard = new ResponseCard(attachmentArray, 1, "application/vnd.amazonaws.card.generic");
		return responseCard;
	}

	private ResponseCard processCardListResponse(ArrayList cardList) {
		Attachment attachmentArray[] = new Attachment[3];
		Attachment attachment;
		Button button;
		Card card;
		for (int i = 0; i < cardList.size(); i++) {
			Button buttonArray[] = new Button[1];
			card = (Card) cardList.get(i);
			button = new Button(card.getCard(), card.getCard().toLowerCase().replaceAll("\\s", ""));
			buttonArray[0] = button;
			attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select", card.getCardThumbnailImage(), card.getCardThumbnailLink());
			//attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select");
			//attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select", "/image/credit_card.png", "www.cap-bank.us");
			attachmentArray[i] = attachment;
		}
		ResponseCard responseCard = new ResponseCard(attachmentArray, 1, "application/vnd.amazonaws.card.generic");
		return responseCard;
	}
	
	/**
	 * Dummy Method
	 * @return
	 */
//	private ResponseCard processResponse() {
//
//		Card card1;
//		
//		//Code when service not working
//		String dummyCardString = "Silver Card;Gold Card;Platinum card";
//		String[] tokens = dummyCardString.split(";");
//		ArrayList cardList = new ArrayList();
//	        for (String token : tokens)
//	        {
//	        	card1 = new Card(token,"www.capgemini.com");
//	        	cardList.add(card1);
//	        }
//	    //Code when service not working
//	    
//	   Attachment attachmentArray[] = new Attachment[3];
//	   Attachment attachment;
//	   Button button;
//	    Card card;
//		for (int i = 0; i < cardList.size(); i++) {
//			Button buttonArray[] = new Button[1];
//			card = (Card) cardList.get(i);
//			button = new Button(card.getCard(), card.getCard().toLowerCase().replaceAll("\\s", ""));
//			buttonArray[0] = button;
//			//attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select", card.getCardThumbnail(),"");
//			attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select");
//			attachmentArray[i] = attachment;
//		}
//		ResponseCard responseCard = new ResponseCard(attachmentArray, 1, "application/vnd.amazonaws.card.generic");
//		return responseCard;
//	}

}
