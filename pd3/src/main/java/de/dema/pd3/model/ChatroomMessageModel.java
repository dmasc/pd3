package de.dema.pd3.model;

import java.time.LocalDateTime;

public class ChatroomMessageModel {

	private String sender;
	
	private Long senderId;
	
	private LocalDateTime sendTimestamp;
	
	private String text;

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public LocalDateTime getSendTimestamp() {
		return sendTimestamp;
	}

	public void setSendTimestamp(LocalDateTime sendTimestamp) {
		this.sendTimestamp = sendTimestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	
}
