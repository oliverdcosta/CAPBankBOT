package com.amazonaws.lambda.capbankbot.beans;

public class LexRequest {
	
	private String botName;
    private String intentName;
	private String creditcard;
    private String agreetoanswer;
	private String userscore;
	private String featureintent;
	private String thanks;
	private String filltypeintent;
	private String usercardintent;
	
	public String getBotName() {
		return botName;
	}
	public void setBotName(String botName) {
		this.botName = botName;
	}
	public String getIntentName() {
		return intentName;
	}
	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}
	public String getCreditcard() {
		return creditcard;
	}
	public void setCreditcard(String creditcard) {
		this.creditcard = creditcard;
	}
	public String getAgreetoanswer() {
		return agreetoanswer;
	}
	public void setAgreetoanswer(String agreetoanswer) {
		this.agreetoanswer = agreetoanswer;
	}
	public String getUserscore() {
		return userscore;
	}
	public void setUserscore(String userscore) {
		this.userscore = userscore;
	}
	public String getFeatureintent() {
		return featureintent;
	}
	public void setFeatureintent(String featureintent) {
		this.featureintent = featureintent;
	}
	public String getThanks() {
		return thanks;
	}
	public void setThanks(String thanks) {
		this.thanks = thanks;
	}
	public String getFilltypeintent() {
		return filltypeintent;
	}
	public void setFilltypeintent(String filltypeintent) {
		this.filltypeintent = filltypeintent;
	}
	public String getUsercardintent() {
		return usercardintent;
	}
	public void setUsercardintent(String usercardintent) {
		this.usercardintent = usercardintent;
	}
	
	
    
	
	
}
