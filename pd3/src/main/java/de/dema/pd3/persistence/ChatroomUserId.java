package de.dema.pd3.persistence;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class ChatroomUserId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private Chatroom chatroom;
	
	@ManyToOne
	private User user;
	
	public ChatroomUserId() {		
	}

	public ChatroomUserId(Chatroom chatroom, User user) {
		this.chatroom = chatroom;
		this.user = user;
	}

	public Chatroom getChatroom() {
		return chatroom;
	}

	public void setChatroom(Chatroom chatroom) {
		this.chatroom = chatroom;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chatroom == null) ? 0 : chatroom.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatroomUserId other = (ChatroomUserId) obj;
		if (chatroom == null) {
			if (other.chatroom != null)
				return false;
		} else if (!chatroom.equals(other.chatroom))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
