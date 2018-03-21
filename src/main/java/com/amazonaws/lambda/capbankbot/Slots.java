package com.amazonaws.lambda.capbankbot;

public class Slots {
	
	private String creditcard;
    private String agreetoanswer;
	private String userscore;
	private String featureintent;
	private String thanks;
	private String filltypeintent;
	private String usercardintent;
		
	
	public Slots(String creditcard, String agreetoanswer, String userscore, String featureintent, String thanks,
			 String usercardintent,String filltypeintent) {
		this.creditcard = creditcard;
		this.agreetoanswer = agreetoanswer;
		this.userscore = userscore;
		this.featureintent = featureintent;
		this.thanks = thanks;
		this.usercardintent = usercardintent;
		this.filltypeintent = filltypeintent;
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

	public String getUsercardintent() {
		return usercardintent;
	}
	public void setUsercardintent(String usercardintent) {
		this.usercardintent = usercardintent;
	}
	public String getFilltypeintent() {
		return filltypeintent;
	}
	public void setFilltypeintent(String filltypeintent) {
		this.filltypeintent = filltypeintent;
	}
	

}
