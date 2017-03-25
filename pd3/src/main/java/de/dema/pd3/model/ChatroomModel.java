package de.dema.pd3.model;

import java.time.LocalDateTime;

public class ChatroomModel {

	private String name;
	
	private Long id;
	
	private LocalDateTime sendTimestamp;
	
	private int unreadMessagesCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getSendTimestamp() {
		return sendTimestamp;
	}

	public void setSendTimestamp(LocalDateTime sendTimestamp) {
		this.sendTimestamp = sendTimestamp;
	}

	public int getUnreadMessagesCount() {
		return unreadMessagesCount;
	}

	public void setUnreadMessagesCount(int unreadMessagesCount) {
		this.unreadMessagesCount = unreadMessagesCount;
	}
	
}
