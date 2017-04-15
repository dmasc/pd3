package de.dema.pd3.model;

import java.time.LocalDateTime;

import de.dema.pd3.persistence.Message;

/**
 * Model für eine einzelne Nachricht in einem Chatraum.
 */
public class ChatroomMessageModel {

	private Long id;
	
	private NamedIdModel sender;
	
	private LocalDateTime sendTimestamp;
	
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NamedIdModel getSender() {
		return sender;
	}

	public void setSender(NamedIdModel sender) {
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

	public static ChatroomMessageModel map(Message message) {
		ChatroomMessageModel model = new ChatroomMessageModel();
		model.setId(message.getId());
		model.setSender(NamedIdModel.map(message.getSender()));
		model.setSendTimestamp(message.getSendTimestamp());
		model.setText(message.getText());
		
		return model;
	}
	
}
