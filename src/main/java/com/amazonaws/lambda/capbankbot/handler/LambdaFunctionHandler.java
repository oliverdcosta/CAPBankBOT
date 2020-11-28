package com.amazonaws.lambda.capbankbot.handler;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import com.amazonaws.lambda.capbankbot.util.*;
import com.amazonaws.lambda.capbankbot.beans.Attachment;
import com.amazonaws.lambda.capbankbot.beans.Button;
import com.amazonaws.lambda.capbankbot.beans.Card;
import com.amazonaws.lambda.capbankbot.beans.DialogAction;
import com.amazonaws.lambda.capbankbot.beans.LexResponse;
import com.amazonaws.lambda.capbankbot.beans.Message;
import com.amazonaws.lambda.capbankbot.beans.ResponseCard;
import com.amazonaws.lambda.capbankbot.beans.Slots;
import com.amazonaws.lambda.capbankbot.util.AEMService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.File;

public class LambdaFunctionHandler implements RequestHandler<Map<String, Object>, Object> {

	@Override
	public Object handleRequest(Map<String, Object> input, Context context) {

		try {

			// LOAD PROPERTY FILE
			File file = new File("awschatbot.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			// RETRIEVE USER ID
			String userId = (String) input.get(properties.getProperty(CapBotConstants.USER_ID));
			AEMService aemService = new AEMService();
			String userDetailString = aemService.getUserProfileInfo(userId);
			int indexOfFNameStart = userDetailString.indexOf(":") + 2;
			int indexOfFNameEnd = userDetailString.indexOf(",") - 1;
			String firstName = userDetailString.substring(indexOfFNameStart, indexOfFNameEnd);
			String subUserDetailString = userDetailString.substring(indexOfFNameEnd + 3);
			int indexOfLNameStart = subUserDetailString.indexOf(":") + 2;
			int indexOfLNameEnd = subUserDetailString.indexOf(",") - 1;
			String lastName = subUserDetailString.substring(indexOfLNameStart, indexOfLNameEnd);

			// RETRIEVE SLOT STRING
			Map<String, Object> currentIntent = (Map<String, Object>) input
					.get(properties.getProperty(CapBotConstants.CURRENT_INTENT));
			String slotString = currentIntent.get("slots").toString();

			// DEFAULT URL AND DIALOG_ACTION
			ResponseCard responseCard;
			String formUrl = properties.getProperty(CapBotConstants.DEFAULT_FORM_URL);
			DialogAction dialogAction = new DialogAction(properties.getProperty(CapBotConstants.CLOSE),
					properties.getProperty(CapBotConstants.FULFILLED),
					new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
							properties.getProperty(CapBotConstants.DEFAULT_MESSAGE)));
			String NULL = properties.getProperty(CapBotConstants.NULL);

			// CHECK THANKS INTENT
			int equalIndex6 = slotString.indexOf("phone=") + 6;
			String slotString7 = slotString.substring(equalIndex6);
			int commaIndex5 = slotString7.indexOf(",");
			String phone = slotString7.substring(0, commaIndex5);
			if (!(phone.equalsIgnoreCase(NULL))) {
				if (validatePhoneNumber(phone)) {
					String lastMSg = properties.getProperty(CapBotConstants.LAST_MESSAGE);
					Message message = new Message(properties.getProperty(CapBotConstants.PLAINTEXT), lastMSg);
					dialogAction = new DialogAction(properties.getProperty(CapBotConstants.CLOSE),
							properties.getProperty(CapBotConstants.FULFILLED), message);
				} else {
					Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
					String secondTryLastMessage = properties.getProperty(CapBotConstants.SECOND_TRY_LAST_MESSAGE);
					dialogAction = new DialogAction(properties.getProperty(CapBotConstants.ELICIT_SLOT),
							properties.getProperty(CapBotConstants.INTENT_NAME), slots,
							properties.getProperty(CapBotConstants.SLOT_PHONE),
							new Message(properties.getProperty(CapBotConstants.PLAINTEXT), secondTryLastMessage));
				}
			} else {
				// CHECK FILLTYPE INTENT
				int equalIndex5 = slotString.indexOf("filltypeintent=") + 15;
				String slotString6 = slotString.substring(equalIndex5);
				int commaIndex4 = slotString6.indexOf(",");
				String filltypeintent = slotString6.substring(0, commaIndex4);

				if (!(filltypeintent.equalsIgnoreCase(NULL))) {
					if (!(filltypeintent.toLowerCase().indexOf("online") == -1)) {
						LambdaLogger logger = context.getLogger();
						Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
						String formLinkMessage = properties.getProperty(CapBotConstants.FORM_LINK_MSG_PART_1) + formUrl
								+ properties.getProperty(CapBotConstants.FORM_LINK_MSG_PART_2) + firstName
								+ properties.getProperty(CapBotConstants.FORM_LINK_MSG_PART_3);
						dialogAction = new DialogAction(properties.getProperty(CapBotConstants.CLOSE),
								properties.getProperty(CapBotConstants.FULFILLED),
								new Message(properties.getProperty(CapBotConstants.PLAINTEXT), formLinkMessage));

					} else if (!(filltypeintent.toLowerCase().indexOf("call") == -1)) {
						Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
						String formLinkMessage = properties.getProperty(CapBotConstants.SECOND_FORM_LINK_MSG);
						dialogAction = new DialogAction(properties.getProperty(CapBotConstants.ELICIT_SLOT),
								properties.getProperty(CapBotConstants.INTENT_NAME), slots,
								properties.getProperty(CapBotConstants.SLOT_PHONE),
								new Message(properties.getProperty(CapBotConstants.PLAINTEXT), formLinkMessage));

					} else {
						Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
						String fillIntentMessage = properties.getProperty(CapBotConstants.SECOND_FILL_INTENT_MSG_PART_1)
								+ firstName + properties.getProperty(CapBotConstants.SECOND_FILL_INTENT_MSG_PART_2);
						dialogAction = new DialogAction(properties.getProperty(CapBotConstants.ELICIT_SLOT),
								properties.getProperty(CapBotConstants.INTENT_NAME), slots,
								properties.getProperty(CapBotConstants.SLOT_FILLTYPEINTENT),
								new Message(properties.getProperty(CapBotConstants.PLAINTEXT), fillIntentMessage));
					}
				} else {
					// CHECK USER CARD INTENT
					int equalIndex4 = slotString.indexOf("usercardintent=") + 15;
					String slotString5 = slotString.substring(equalIndex4);
					int commaIndex3 = slotString5.indexOf(",");
					String usercardintent = slotString5.substring(0, commaIndex3);

					if (!(usercardintent.equalsIgnoreCase(NULL))) {
						if (!(usercardintent.indexOf(CapBotConstants.ELITE) == -1)
								|| !(usercardintent.indexOf(CapBotConstants.PREFERRED) == -1)
								|| !(usercardintent.indexOf(CapBotConstants.PRIORITY) == -1)) {
							// AEMService aemService = new AEMService();
							formUrl = aemService.getCardRegisterFormLink(firstName, lastName, null, null,
									usercardintent);
							formUrl = formUrl + usercardintent;
							LambdaLogger logger = context.getLogger();
							Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
							String usercardlink = "";
							if (!(usercardintent.indexOf(CapBotConstants.ELITE) == -1)) {
								usercardlink = CapBotConstants.ELITE;
							}
							if (!(usercardintent.indexOf(CapBotConstants.PREFERRED) == -1)) {
								usercardlink = CapBotConstants.PREFERRED;
							}
							if (!(usercardintent.indexOf(CapBotConstants.PRIORITY) == -1)) {
								usercardlink = CapBotConstants.PRIORITY_FIRST;
							}
							String fillIntentMessage = CapBotConstants.FILL_INTENT_MSG_PART_1 + firstName
									+ CapBotConstants.FILL_INTENT_MSG_PART_2 + usercardlink + ".html"
									+ CapBotConstants.FILL_INTENT_MSG_PART_3;
							dialogAction = new DialogAction(properties.getProperty(CapBotConstants.ELICIT_SLOT),
									properties.getProperty(CapBotConstants.INTENT_NAME), slots,
									properties.getProperty(CapBotConstants.SLOT_FILLTYPEINTENT),
									new Message(properties.getProperty(CapBotConstants.PLAINTEXT), fillIntentMessage));
						} else {
							dialogAction = new DialogAction(properties.getProperty(CapBotConstants.CLOSE),
									properties.getProperty(CapBotConstants.FULFILLED),
									new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
											properties.getProperty(CapBotConstants.FINAL_MESSAGE)));
						}

					} else {
						// featureIntent
						int equalIndex3 = slotString.indexOf("featureintent=") + 14;

						String slotString4 = slotString.substring(equalIndex3);

						int endBrcktIndex2 = slotString4.indexOf("}");
						String featureIntent = slotString4.substring(0, endBrcktIndex2);

						if (!(featureIntent.equalsIgnoreCase(NULL))) {
							LambdaLogger logger = context.getLogger();

							String featureIntentArg = CapBotConstants.CAP_BANK;
							// Service
							if (featureIntent.equalsIgnoreCase(CapBotConstants.CASHREWARDS)) {
								featureIntentArg = CapBotConstants.CASH_REWARDS;
							}
							if (featureIntent.equalsIgnoreCase(CapBotConstants.HOTELREWARDS)) {
								featureIntentArg = CapBotConstants.HOTEL_REWARDS;
							}
							if (featureIntent.equalsIgnoreCase(CapBotConstants.TRAVELMILES)) {
								featureIntentArg = CapBotConstants.TRAVEL_MILES;
							}
							JSONArray creditcardOffers = aemService.getCreditCardOffers(featureIntentArg);
							String elementString = "";
							ArrayList<Object> cardList = new ArrayList<Object>();
							Card card;
							for (int j = 0, size = creditcardOffers.length(); j < size; j++) {
								JSONObject objectInArray = creditcardOffers.getJSONObject(j);
								String[] elementNames = JSONObject.getNames(objectInArray);
								card = new Card(objectInArray.getString("product_name"),
										objectInArray.getString("thumbnail_path"),
										objectInArray.getString("page_link"));
								cardList.add(card);
								System.out.println();
							}
							responseCard = processCardListResponse(cardList);

							String feature = null;

							if (!(featureIntent.indexOf("cash") == -1) || !(featureIntent.indexOf("travel") == -1)
									|| !(featureIntent.indexOf("hotel") == -1)
									|| !(featureIntent.indexOf("miles") == -1)) {

								Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
								dialogAction = new DialogAction(properties.getProperty(CapBotConstants.ELICIT_SLOT),
										properties.getProperty(CapBotConstants.INTENT_NAME), slots,
										CapBotConstants.SLOT_USERCARDINTENT, responseCard,
										new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
												CapBotConstants.CARD_MESSAGE));
							} else {
								dialogAction = new DialogAction(properties.getProperty(CapBotConstants.CLOSE),
										properties.getProperty(CapBotConstants.FULFILLED),
										new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
												properties.getProperty(CapBotConstants.CARD_FINAL_MESSAGE)));
							}

						} else {
							// Score
							int equalIndex2 = slotString.indexOf("userscore=") + 10;
							String slotString3 = slotString.substring(equalIndex2);
							int commaIndex2 = slotString3.indexOf(",");
							String userscore = slotString3.substring(0, commaIndex2);

							if (!(userscore.equalsIgnoreCase(NULL))) {
								if (userscore.equalsIgnoreCase("500") || userscore.equalsIgnoreCase("800")
										|| userscore.equalsIgnoreCase("500 - 800")) {
									Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
									responseCard = processFeatureCard(userscore, firstName);
									dialogAction = new DialogAction(properties.getProperty(CapBotConstants.ELICIT_SLOT),
											properties.getProperty(CapBotConstants.INTENT_NAME), slots,
											CapBotConstants.SLOT_FEATUREINTENT, responseCard);
								} else {
									String scoreErrorMessage = properties
											.getProperty(CapBotConstants.SCORE_ERROR_MESSAGE);
									responseCard = processScoreResponsecard();
									Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
									dialogAction = new DialogAction(properties.getProperty(CapBotConstants.ELICIT_SLOT),
											properties.getProperty(CapBotConstants.INTENT_NAME), slots,
											CapBotConstants.SLOT_SCORE, responseCard,
											new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
													scoreErrorMessage));
								}
							} else {
								// Agrre to Answer
								int equalIndex = slotString.indexOf("agreetoanswer=") + 14;
								String slotString2 = slotString.substring(equalIndex);
								int commaIndex = slotString2.indexOf(",");
								String agreeToAnswer = slotString2.substring(0, commaIndex);
								if (!(agreeToAnswer.equalsIgnoreCase(NULL))) {
									if (agreeToAnswer.equalsIgnoreCase("no")) {
										Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
										dialogAction = new DialogAction(
												properties.getProperty(CapBotConstants.ELICIT_SLOT),
												properties.getProperty(CapBotConstants.INTENT_NAME), slots,
												CapBotConstants.SLOT_AGREETOANSWER,
												new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
														properties.getProperty(CapBotConstants.AGREE_MESSAGE)));
									} else {
										responseCard = processScoreResponsecard();
										Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
										dialogAction = new DialogAction(
												properties.getProperty(CapBotConstants.ELICIT_SLOT),
												properties.getProperty(CapBotConstants.INTENT_NAME), slots,
												CapBotConstants.SLOT_SCORE, responseCard);
									}
								} else {
									int equalIndex0 = slotString.indexOf("creditcard=") + 11;
									String slotString0 = slotString.substring(equalIndex0);
									int commaIndex0 = slotString0.indexOf(",");
									String creditcardintent = slotString0.substring(0, commaIndex0);
									if (!(creditcardintent.equalsIgnoreCase(NULL))) {
										if (creditcardintent.toLowerCase().indexOf("credit") != -1) {
											Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
											dialogAction = new DialogAction(
													properties.getProperty(CapBotConstants.ELICIT_SLOT),
													properties.getProperty(CapBotConstants.INTENT_NAME), slots,
													CapBotConstants.SLOT_AGREETOANSWER,
													new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
															properties.getProperty(CapBotConstants.HELP_MESSAGE_1)
																	+ firstName + properties.getProperty(
																			CapBotConstants.HELP_MESSAGE_2)));
										} else {

											Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
											dialogAction = new DialogAction(
													properties.getProperty(CapBotConstants.CLOSE),
													properties.getProperty(CapBotConstants.FULFILLED),
													new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
															properties.getProperty(
																	CapBotConstants.SECOND_FILL_INTENT_MSG_PART_1)
																	+ firstName + properties.getProperty(
																			CapBotConstants.SECOND_HELP_MESSAGE)));
										}
									} else {
										Slots slots = new Slots(NULL, NULL, NULL, NULL, NULL, NULL, NULL);
										String formLinkMessage = properties
												.getProperty(CapBotConstants.FINAL_SORRY_MESSAGE);
										dialogAction = new DialogAction(
												properties.getProperty(CapBotConstants.ELICIT_SLOT),
												properties.getProperty(CapBotConstants.INTENT_NAME), slots,
												CapBotConstants.SLOT_PHONE,
												new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
														formLinkMessage));
									}

								}
							}
						}
					}
				}
			}
			return new LexResponse(dialogAction);
		} catch (Exception e) {
			try {
				File file = new File("awschatbot.properties");
				FileInputStream fileInput = new FileInputStream(file);
				Properties properties = new Properties();
				properties.load(fileInput);
				fileInput.close();
				DialogAction dialogAction = new DialogAction(properties.getProperty(CapBotConstants.CLOSE),
						properties.getProperty(CapBotConstants.FULFILLED),
						new Message(properties.getProperty(CapBotConstants.PLAINTEXT),
								properties.getProperty(CapBotConstants.CLOSING_MESSAGE)));
				return new LexResponse(dialogAction);
			} catch (Exception ex) {
				DialogAction dialogAction = new DialogAction("Closed", "Fulfilled", new Message("", ""));
				return new LexResponse(dialogAction);
			}

		}

	}

	/**
	 * Method to build response card for asking and taking scope information
	 * 
	 * @return
	 */
	private ResponseCard processScoreResponsecard() {
		ResponseCard responseCard = null;
		try {
			File file = new File("awschatbot.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
		Button buttonArray[] = new Button[3];
		Button button = null;
		button = new Button("750+ - Excellent", "800");
		buttonArray[0] = button;
		button = new Button("600 to 750 – Good", "500 - 800");
		buttonArray[1] = button;
		button = new Button("600 or below - Poor", "500");
		buttonArray[2] = button;
		Attachment attachmentArray[] = new Attachment[1];
		attachmentArray[0] = new Attachment(buttonArray, properties.getProperty(CapBotConstants.RC_SCORE_Q1), properties.getProperty(CapBotConstants.RC_SCORE_Q2));
		responseCard = new ResponseCard(attachmentArray, 1, CapBotConstants.RESPONSE_CARD_TYPE);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return responseCard;
	}

	/**
	 * Method to build the response card with available features
	 * 
	 * @param userscore
	 * @param firstName
	 * @return
	 */
	private ResponseCard processFeatureCard(String userscore, String firstName) {
		ResponseCard responseCard = null;
		try {
			File file = new File("awschatbot.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			Button buttonArray[] = new Button[3];
			Button button = null;
			button = new Button(CapBotConstants.TRAVEL_MILES, CapBotConstants.TRAVELMILES);
			buttonArray[0] = button;
			button = new Button(CapBotConstants.CASH_REWARDS, CapBotConstants.CASHREWARDS);
			buttonArray[1] = button;
			button = new Button(CapBotConstants.HOTEL_REWARDS, CapBotConstants.HOTELREWARDS);
			buttonArray[2] = button;
			Attachment attachmentArray[] = new Attachment[1];
			attachmentArray[0] = new Attachment(buttonArray, properties.getProperty(CapBotConstants.AVAILABLE_FEATURES),
					properties.getProperty(CapBotConstants.SELECT_FEATURE));
			responseCard = new ResponseCard(attachmentArray, 1, CapBotConstants.RESPONSE_CARD_TYPE);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return responseCard;

	}

	/**
	 * Method to build the response card with credit cards in offer
	 * 
	 * @param cardList
	 * @return
	 */
	private ResponseCard processCardListResponse(ArrayList cardList) {
		ResponseCard responseCard = null;
		try {
			File file = new File("awschatbot.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
		Attachment attachmentArray[] = new Attachment[3];
		Attachment attachment;
		Button button;
		Card card;
		for (int i = 0; i < cardList.size(); i++) {
			Button buttonArray[] = new Button[1];
			card = (Card) cardList.get(i);
			button = new Button(card.getCard(), card.getCard().toLowerCase().replaceAll("\\s", ""));
			buttonArray[0] = button;
			attachment = new Attachment(buttonArray, card.getCard(), properties.getProperty(CapBotConstants.CARD_SELECT_MESSAGE),
					card.getCardThumbnailImage(), card.getCardThumbnailLink());
			attachmentArray[i] = attachment;
		}
		responseCard = new ResponseCard(attachmentArray, 1, CapBotConstants.RESPONSE_CARD_TYPE);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return responseCard;
	}

	/**
	 * This method validates the phone number provided by user in chat
	 * 
	 * @param phoneNo
	 * @return
	 */
	private boolean validatePhoneNumber(String phoneNo) {
		// validate phone numbers of format "1234567890"
		if (phoneNo.matches("\\d{10}"))
			return true;
		// validating phone number with -, . or spaces
		else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
			return true;
		// validating phone number with extension length from 3 to 5
		else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
			return true;
		// validating phone number where area code is in braces ()
		else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
			return true;
		// return false if nothing matches the input
		else
			return false;

	}

}
