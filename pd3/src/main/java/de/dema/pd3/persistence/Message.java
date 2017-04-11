package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private User sender;
	
	private LocalDateTime sendTimestamp;
	
	@Lob
	private String text;

	@ManyToOne
	private Chatroom room;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
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

	public Chatroom getRoom() {
		return room;
	}

	public void setRoom(Chatroom room) {
		this.room = room;
	}

	public static class Builder {
		
		private Message msg = new Message();
		
		public Builder sender(User sender) {
			msg.sender = sender;
			return this;
		}

		public Builder sendTimestamp(LocalDateTime sendTimestamp) {
			msg.sendTimestamp = sendTimestamp;
			return this;
		}

		public Builder text(String text) {
			msg.text = text;
			return this;
		}

		public Builder room(Chatroom room) {
			msg.room = room;
			return this;
		}
		
		public Message build() {
			return msg;
		}
		
	}
	
}
