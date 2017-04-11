package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Chatroom implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany(mappedBy = "id.chatroom", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<ChatroomUser> users;
	
	@OneToMany(mappedBy = "room", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<Message> messages;
	
	private LocalDateTime lastMessageSent;

	public Set<ChatroomUser> getUsers() {
		return users;
	}

	public void setUsers(Set<ChatroomUser> users) {
		this.users = users;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getLastMessageSent() {
		return lastMessageSent;
	}

	public void setLastMessageSent(LocalDateTime lastMessageSent) {
		this.lastMessageSent = lastMessageSent;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}
	
}
