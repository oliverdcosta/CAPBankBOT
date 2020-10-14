package com.amazonaws.lambda.capbankbot.beans;

public class Card {
	public String card;
	public String cardThumbnailImage;
	public String cardThumbnailLink;
	
	
	public Card(String card, String cardThumbnailImage, String cardThumbnailLink) {
		this.card = card;
		this.cardThumbnailImage = cardThumbnailImage;
		this.cardThumbnailLink = cardThumbnailLink;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getCardThumbnailImage() {
		return cardThumbnailImage;
	}
	public void setCardThumbnailImage(String cardThumbnailImage) {
		this.cardThumbnailImage = cardThumbnailImage;
	}
	public String getCardThumbnailLink() {
		return cardThumbnailLink;
	}
	public void setCardThumbnailLink(String cardThumbnailLink) {
		this.cardThumbnailLink = cardThumbnailLink;
	}

}
