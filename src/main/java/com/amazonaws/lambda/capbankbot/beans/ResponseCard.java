package com.amazonaws.lambda.capbankbot.beans;

public class ResponseCard {
	
    private Attachment[] genericAttachments;
    private int version;
    private String contentType;
    
	public ResponseCard(Attachment[] genericAttachments, int version, String contentType) {
		this.genericAttachments = genericAttachments;
		this.version = version;
		this.contentType = contentType;
	}
	public Attachment[] getGenericAttachments() {
		return genericAttachments;
	}
	public void setGenericAttachments(Attachment[] genericAttachments) {
		this.genericAttachments = genericAttachments;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
