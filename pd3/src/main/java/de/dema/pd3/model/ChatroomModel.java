package de.dema.pd3.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.dema.pd3.Pd3Util;
import de.dema.pd3.persistence.ChatroomUser;

public class ChatroomModel extends NamedIdModel implements Comparable<ChatroomModel> {

	private LocalDateTime lastMessageSentTimestamp;
	
	private int unreadMessagesCount;
	
	private boolean notificationsActive;
	
	private List<NamedIdModel> members;
	
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

	public List<NamedIdModel> getMembers() {
		return members;
	}

	public void setMembers(List<NamedIdModel> members) {
		this.members = members;
	}

	public boolean isCustomName() {
		return super.getName() != null;
	}

	@Override
	public String getName() {
		if (isCustomName()) {
			return super.getName();
		}
		return getMembers().toString();
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
		model.setName(room.getName());
		model.setMembers(room.getChatroom().getUsers().stream()
					.filter(u -> u.getUser().getId() != room.getUser().getId())
					.map(u -> new NamedIdModel(u.getUser().getId(), Pd3Util.username(u.getUser())))
					.sorted((n1, n2) -> n1.getName().compareTo(n2.getName()))
					.collect(Collectors.toList()));
		
		return model;
	}

}
