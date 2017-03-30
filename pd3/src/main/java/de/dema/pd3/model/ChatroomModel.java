package de.dema.pd3.model;

import java.time.LocalDateTime;

public class ChatroomModel extends NamedIdModel {

	private LocalDateTime sendTimestamp;
	
	private int unreadMessagesCount;

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
