package de.dema.pd3.model;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.dema.pd3.persistence.ChatroomUser;

public class ChatroomModel extends NamedIdModel implements Comparable<ChatroomModel> {

	private LocalDateTime lastMessageSentTimestamp;
	
	private int unreadMessagesCount;
	
	private boolean notificationsActive;

	public LocalDateTime getLastMessageSentTimestamp() {
		return lastMessageSentTimestamp;
	}

	public void setLastMessageSentTimestamp(LocalDateTime lastMessageSentTimestamp) {
		this.lastMessageSentTimestamp = lastMessageSentTimestamp;
	}

	public int getUnreadMessagesCount() {
		return unreadMessagesCount;
	}

	public void setUnreadMessagesCount(int unreadMessagesCount) {
		this.unreadMessagesCount = unreadMessagesCount;
	}

	public boolean isNotificationsActive() {
		return notificationsActive;
	}

	public void setNotificationsActive(boolean notificationsActive) {
		this.notificationsActive = notificationsActive;
	}

	@Override
	public int compareTo(ChatroomModel o) {
		return o.getLastMessageSentTimestamp().compareTo(getLastMessageSentTimestamp());
	}
	
	public static ChatroomModel map(ChatroomUser room, Function<ChatroomUser, Integer> unreadMessageCountReader) {
		ChatroomModel model = new ChatroomModel();
		model.setId(room.getId().getChatroom().getId());
		model.setLastMessageSentTimestamp(room.getChatroom().getLastMessageSent());
		model.setNotificationsActive(room.isNotificationsActive());
		model.setUnreadMessagesCount(unreadMessageCountReader.apply(room));
		if (room.getName() != null) {
			model.setName(room.getName());
		} else {
			model.setName(room.getChatroom().getUsers().stream()
					.filter(u -> u.getUser().getId() != room.getUser().getId())
					.map(u -> u.getUser().getSurname())
					.sorted().collect(Collectors.toList()).toString());
		}
		
		return model;
	}

}
