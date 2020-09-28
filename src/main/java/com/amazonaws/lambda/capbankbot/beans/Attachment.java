package com.amazonaws.lambda.capbankbot.beans;

public class Attachment {
	
    private Button[] buttons;
    private String subTitle;
    private String title;
    private String imageUrl;
    private String attachmentLinkUrl;
    		
//test pipeline 2
	public Attachment(Button[] buttons, String subTitle, String title, String imageUrl, String attachmentLinkUrl) {
		this.buttons = buttons;
		this.subTitle = subTitle;
		this.title = title;
		this.imageUrl = imageUrl;
		this.attachmentLinkUrl = attachmentLinkUrl;
	}
	
	public Attachment(Button[] buttons, String subTitle, String title) {
		this.buttons = buttons;
		this.subTitle = subTitle;
		this.title = title;
	}
	
	public Button[] getButtons() {
		return buttons;
	}
	public void setButtons(Button[] buttons) {
		this.buttons = buttons;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAttachmentLinkUrl() {
		return attachmentLinkUrl;
	}

	public void setAttachmentLinkUrl(String attachmentLinkUrl) {
		this.attachmentLinkUrl = attachmentLinkUrl;
	}
	

	
}
