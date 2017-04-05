package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class ChatroomUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ChatroomUserId id;
	
	private String name;
	
	private LocalDateTime lastMessageRead;
	
	private boolean notificationsActive = true;

	public ChatroomUser() {
	}

	public ChatroomUser(User user, Chatroom chatroom) {
		setId(new ChatroomUserId(chatroom, user));
	}
	
	public Chatroom getChatroom() {
		return id != null ? id.getChatroom() : null;
	}

	public User getUser() {
		return id != null ? id.getUser() : null;
	}

	public LocalDateTime getLastMessageRead() {
		return lastMessageRead;
	}

	public void setLastMessageRead(LocalDateTime lastMessageRead) {
		this.lastMessageRead = lastMessageRead;
	}

	public boolean isNotificationsActive() {
		return notificationsActive;
	}

	public void setNotificationsActive(boolean notificationsActive) {
		this.notificationsActive = notificationsActive;
	}

	public ChatroomUserId getId() {
		return id;
	}

	public void setId(ChatroomUserId id) {
		this.id = id;
	}

	public void setId(Chatroom chatroom, User user) {
		setId(new ChatroomUserId(chatroom, user));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
