package de.dema.pd3.model;

import java.time.LocalDateTime;

import de.dema.pd3.Pd3Util;
import de.dema.pd3.persistence.Message;

/**
 * Model für eine einzelne Nachricht in einem Chatraum.
 */
public class ChatroomMessageModel {

	private Long id;
	
	private String sender;
	
	private Long senderId;
	
	private LocalDateTime sendTimestamp;
	
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public static ChatroomMessageModel map(Message message) {
		ChatroomMessageModel model = new ChatroomMessageModel();
		model.setId(message.getId());
		model.setSender(Pd3Util.username(message.getSender()));
		model.setSenderId(message.getSender().getId());
		model.setSendTimestamp(message.getSendTimestamp());
		model.setText(message.getText());
		
		return model;
	}
	
}
