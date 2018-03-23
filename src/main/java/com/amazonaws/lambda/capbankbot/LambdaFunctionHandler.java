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

	String formUrl = "https://www.cap-bank.us/";

	@Override
	public Object handleRequest(Map<String, Object> input, Context context) {
		int i = 0;
		String userId = (String) input.get("userId");
		AEMService aemService = new AEMService();
		String userDetailString = aemService.getUserProfileInfo(userId);
		
		int indexOfFNameStart = userDetailString.indexOf(":")+2;
		int indexOfFNameEnd = userDetailString.indexOf(",")-1;
		String firstName = userDetailString.substring(indexOfFNameStart,indexOfFNameEnd);
		
		String subUserDetailString = userDetailString.substring(indexOfFNameEnd+3);
	
		int indexOfLNameStart = subUserDetailString.indexOf(":")+2;
		int indexOfLNameEnd = subUserDetailString.indexOf(",")-1;
		String lastName = subUserDetailString.substring(indexOfLNameStart,indexOfLNameEnd);
				
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
			int equalIndex6 = responseToLexMsg.indexOf("phone=") + 6;
			i = i + 1;
			String responseToLexMsg7 = responseToLexMsg.substring(equalIndex6);
			i = i + 1;
			int commaIndex5 = responseToLexMsg7.indexOf(",");
			String phone = responseToLexMsg7.substring(0, commaIndex5);
			i = i + 1;
			if (!(phone.equalsIgnoreCase("null"))) {
				String lastMSg = "Thanks. We are experiencing more than normal call volumes, a live agent will call you in approx. 20 minutes. Hope I was able to help. Please visit us at www.cap-bank.us for all your banking needs." ;
				Message message = new Message("PlainText", lastMSg);
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
					if(!(filltypeintent.indexOf("online") == -1)) {
						String defaultLink = "http://54.195.246.137/services/JsonResponceServlet?fName=karthik&sName=bajjuri&email=abc@gm.co&phone=12345678&preference=loan";
						LambdaLogger logger = context.getLogger();
						Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
						String formLinkMessage = "Please complete the application here – " + formUrl +" Hope I was able to help , "+firstName+". Please visit us at www.cap-bank.us for all your banking needs. ";
						dialogAction = new DialogAction("Close", "Fulfilled",
								new Message("PlainText", formLinkMessage));
						i = i + 1;
					}
					if(!(filltypeintent.indexOf("call") == -1)) {
						Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
						String formLinkMessage = " Please share your contact number and a Live agent will give you a call";
						dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots, "phone",
								new Message("PlainText", formLinkMessage));
						i = i + 1;
					}
					
					
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
						if(!(usercardintent.indexOf("elite") == -1)||!(usercardintent.indexOf("preferred") == -1)||!(usercardintent.indexOf("priority") == -1)) {
//							AEMService aemService = new AEMService();
							formUrl = aemService.getCardRegisterFormLink(firstName, lastName, "12345678", "abc@cap.com",
									usercardintent);
							formUrl = formUrl + usercardintent;
							LambdaLogger logger = context.getLogger();
							Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
							String fillIntentMessage = "Great "+firstName+", please refer to the card details at www.cap-bank.us/"+usercardintent
									+ "; Would you like to complete the application on our website or would you like us to give you call now.";
							dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots, "filltypeintent",
									new Message("PlainText", fillIntentMessage));
						}else {
							dialogAction = new DialogAction("Close", "Fulfilled",
									new Message("PlainText", "For a complete list of credit cards, please visit – www.cap-bank.us/credit-cards; Please select a card from the list and we can help you complete the application\r\n"));
						}
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
							JSONArray creditcardOffers = aemService.getCreditCardOffers(featureIntent);
							String elementString = "";
							ArrayList<Object> cardList = new ArrayList<Object>();
							Card card;
							for (int j = 0, size = creditcardOffers.length(); j < size; j++)
						    {
						      JSONObject objectInArray = creditcardOffers.getJSONObject(j);
						      String[] elementNames = JSONObject.getNames(objectInArray);
						      System.out.printf("%d ELEMENTS IN CURRENT OBJECT:\n", elementNames.length);
						      card = new Card(objectInArray.getString("product_name"), objectInArray.getString("thumbnail_path"),objectInArray.getString("page_link"));
						      cardList.add(card);
						      System.out.println();
						    }
							responseCard = processCardListResponse(cardList);
							
							String feature = null;
							
													
							if(!(featureIntent.indexOf("cash") == -1)||!(featureIntent.indexOf("travel") == -1)||!(featureIntent.indexOf("hotel") == -1)||!(featureIntent.indexOf("miles") == -1)) {
								
				
								
								//dummy
//								if(!(featureIntent.indexOf("cash") == -1)) {
//									feature = "Cash";
//								}
//								if(!(featureIntent.indexOf("travel") == -1)) {
//									feature = "Travel";
//								}
//								if(!(featureIntent.indexOf("hotel") == -1)) {
//									feature = "Hotel";
//								}
								//responseCard = processResponse(feature);
								
								Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
								dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots, "usercardintent", responseCard, new Message("PlainText",
										"Sure. Below are some credit cards which you might like. Please click on a card to know more about it; "
												+ "For a complete list of credit cards, please visit – www.cap-bank.us/credit-cards"));
							}else {
								dialogAction = new DialogAction("Close", "Fulfilled",
										new Message("PlainText", "For a complete list of credit cards, please visit – www.cap-bank.us/credit-cards; Please select a card from the list and we can help you complete the application\r\n"));
							}
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
									String scoreMessage = "Thanks "+firstName+";  you have a good credit score, keep it up. Are you looking for any specific features like Travel Miles, Hotel Rewards, Cash Rewards?";
									if(userscore.equalsIgnoreCase("500")) {
										scoreMessage = "Thanks "+firstName+"; Are you looking for any specific features like Travel miles, hotel miles, cash rewards?";
									}
									dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots,
											"featureintent", new Message("PlainText",
													scoreMessage));
								} else {
									String scoreErrorMessage = "Sorry, I did not understand. Please select your approximate credit score.";
									responseCard = processScoreResponsecard();
									Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
									dialogAction = new DialogAction("ElicitSlot", "FirstCreditIntent", slots,
											"userscore", responseCard, new Message("PlainText",scoreErrorMessage));
									}
							} else {
								// Agrre to Answer
								int equalIndex = responseToLexMsg.indexOf("agreetoanswer=") + 14;
								String responseToLexMsg2 = responseToLexMsg.substring(equalIndex);
								int commaIndex = responseToLexMsg2.indexOf(",");
								String agreeToAnswer = responseToLexMsg2.substring(0, commaIndex);
								if (!(agreeToAnswer.equalsIgnoreCase("null"))) {
									if (agreeToAnswer.equalsIgnoreCase("yes")) {
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
												"agreetoanswer", new Message("PlainText",
														"Sure "+firstName+" ! I can help you with that. Do you mind if I ask you a few questions to help me find the right credit card to match your needs?"));
									} else {
										Slots slots = new Slots("null", "null", "null", "null", "null", "null", "null");
										dialogAction = new DialogAction("Close", "Fulfilled", new Message("PlainText",
												"Sorry "+firstName+"; I did not understand. This service is only to provide information about credit cards we offer and help you apply for a credit card;"));
									}
								}
							}
						}
					}
				}
			}
			return new LexResponse(dialogAction);
		} catch (Exception e) {
			int equalIndex6 = slotString.indexOf("phone=") + 6;
			i = i + 1;
			String responseToLexMsg7 = slotString.substring(equalIndex6);
			i = i + 1;
			int commaIndex5 = responseToLexMsg7.indexOf(",");
			String phone = responseToLexMsg7.substring(0, commaIndex5);
			i = i + 1;
			dialogAction = new DialogAction("Close", "Fulfilled",
					new Message("PlainText", "Sorry , "+phone+"I did not understand.This service is only to provide information about credit cards we offer and help you apply for a credit card; For more information, please visit https://www.cap-bank.us. We are here to help if you need support"));
			return new LexResponse(dialogAction);
		}

	}

	private ResponseCard processScoreResponsecard() {
		// String scoreString = "<500 | 500 to 600 | 600 to 700 | 700 to 800 | 800+";
		Button buttonArray[] = new Button[4];
		Button button = null;
		button = new Button("500 or less - Poor", "500");
		buttonArray[0] = button;
		button = new Button("500 - 800 - Good", "500 - 800");
		buttonArray[1] = button;
		button = new Button("800 or more - Great", "800");
		buttonArray[2] = button;
		button = new Button("1000 or more - Great", "800");
		buttonArray[3] = button;
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
			attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select", card.getCardThumbnailImage(),card.getCardThumbnailLink());
			//attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select","http://www.cap-bank.us/content/capbank-portal/priority-first.html?wcmmode=disabled","");
			attachmentArray[i] = attachment;
		}
		ResponseCard responseCard = new ResponseCard(attachmentArray, 1, "application/vnd.amazonaws.card.generic");
		return responseCard;
	}
	
	/**
	 * Dummy Method
	 * @return
	 */
//	private ResponseCard processResponse(String feature) {
//
//		Card card1;
//		
//		//Code when service not working
//		String dummyCardString = "Elite;Priority First;Preferred Card";
//		String[] tokens = dummyCardString.split(";");
//		ArrayList cardList = new ArrayList();
//	        for (String token : tokens)
//	        {
//	        	card1 = new Card(token,"","");
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
//			if(feature != null) {
//				button = new Button(card.getCard() +"-"+ feature, card.getCard().toLowerCase().replaceAll("\\s", ""));
//				buttonArray[0] = button;
//			}else {
//				button = new Button(card.getCard(), card.getCard().toLowerCase().replaceAll("\\s", ""));
//				buttonArray[0] = button;
//			}
//	    	//attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select", card.getCardThumbnail(),"");
//			attachment = new Attachment(buttonArray, card.getCard(), "Browse and Select");
//			attachmentArray[i] = attachment;
//		}
//		ResponseCard responseCard = new ResponseCard(attachmentArray, 1, "application/vnd.amazonaws.card.generic");
//		return responseCard;
//	}

}
