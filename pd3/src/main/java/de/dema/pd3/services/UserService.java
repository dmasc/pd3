package de.dema.pd3.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.dema.pd3.model.EventModel;
import de.dema.pd3.model.events.EventModelFactory;
import de.dema.pd3.model.events.EventTypes;
import de.dema.pd3.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.dema.pd3.model.ChatroomMessageModel;
import de.dema.pd3.model.ChatroomModel;
import de.dema.pd3.model.RegisterUserModel;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserGroupRepository userGroupRepo;

	@Autowired
	private EventRecipientRepository eventRecipientRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EventService eventService;

	public User registerUser(RegisterUserModel userModel) {
		log.debug("registerUser called [model:{}]", userModel);
		
		User user = new User();
		user.setName(userModel.getForename() + " " + userModel.getSurname());
		user.setBirthday(userModel.getBirthday() != null ? LocalDate.parse(userModel.getBirthday()) : null);
		user.setDistrict(userModel.getDistrict());
		user.setEmail(userModel.getEmail());
		user.setForename(userModel.getForename());
		user.setIdCardNumber(userModel.getIdCardNumber());
		user.setPassword(passwordEncoder.encode(userModel.getPassword()));
		user.setPhone(userModel.getPhone());
		user.setStreet(userModel.getStreet());
		user.setSurname(userModel.getSurname());
		user.setZip(userModel.getZip());
		user.setLastCheckForMessages(LocalDateTime.of(2017, 1, 1, 0, 0));
		
		user = userRepo.save(user);
		log.info("user registered [id:{}]", user.getId());
		return user;
	}
	
	public RegisterUserModel findRegisterUserById(Long id) {
		return RegisterUserModel.map(userRepo.findOne(id));
	}

	/**
	 * Sendet Nachricht <code>text</code> an User mit id <code>recipientUserId</code>.
	 *
	 * Für jeden Dialog wird eine Gruppe erzeugt. Wenn Sender und Empfänger bereits Nachrichten
	 * ausgetauscht haben, wird die bestehende Gruppe weiter benutzt.
	 *
	 * Die Gruppen für solche Dialoge heissen im Frontend <code>ChatroomModel</code>.
	 * Die Nachrichten einer Gruppe heissen im Frontend <code>ChatroomMessageModel</code>.
	 *
	 * @param text nachricht
	 * @param senderId user id des absenders
	 * @param recipientUserId user id des empfängers
	 * @throws Exception
     */
	public void sendMessageToUser(String text, Long senderId, Long recipientUserId) {
		User sendingUser = userRepo.findOne(senderId);
		EventRecipient recipient = eventRecipientRepository.findOne(recipientUserId);
		if (recipient == null) {
			// recipientUserId no exists in User nor UserGroup, try to find chat room by recipient id's
			User receivingUser = userRepo.findOne(recipientUserId);
			recipient = userGroupRepo.findUserGroupForDialogBetweenMembers(sendingUser, receivingUser);
			if (recipient == null) {
				// No chatroom found for recipientUserId, create a new one
				UserGroup recipientGroup = new UserGroup();
				recipientGroup.setName(normalizeDialogGroupName(sendingUser, receivingUser));
				recipientGroup.setCreated(LocalDateTime.now());
				List<User> members = new ArrayList<>();
				members.add(sendingUser);
				members.add(receivingUser);
				recipientGroup.addMembers(members.toArray(new User[members.size()]));
				recipientGroup = userGroupRepo.save(recipientGroup);
				recipient = recipientGroup;
			}
		}
		try {
			eventService.sendEvent(
                    EventModelFactory.createUserMessage(
                            sendingUser.getForename() + " " + sendingUser.getSurname(),
							sendingUser.getId(),
                            text,
							recipient.getId()));
			updateLastCheckForMessagesForChatroom(LocalDateTime.now(), senderId, recipientUserId);
		} catch (Exception e) {
			log.warn("Cannot send event", e);
		}
	}
	
	public List<ChatroomModel> loadAllChatroomsOrderedByTimestampOfLastMessageDesc(Long userId) {
		List<ChatroomModel> rooms = new ArrayList<>();
		User user = userRepo.findOne(userId);
		List<UserGroup> groups = userGroupRepo.findByMembersIn(user);

		// one room for private messages
		ChatroomModel model = new ChatroomModel();
		model.setId(userId);
		model.setName("System Nachrichten");
		model.setUnreadMessagesCount(eventRepository.countEventsByTypeOfRecipient(
				EventTypes.USER_MESSAGE.getId(),
				user.getLastCheckForMessages(),
				user));
		model.setSendTimestamp(eventRepository.findEarliestSendTimeOfEventsByTypeOfRecipient(
				EventTypes.USER_MESSAGE.getId(),
				user));
		rooms.add(model);

		for (UserGroup group : groups) {
			model = new ChatroomModel();
			model.setId(group.getId());
			model.setName(denormalizeDialogGroupName(group.getName(), user));
			model.setUnreadMessagesCount(eventRepository.countEventsByTypeOfRecipient(
					EventTypes.USER_MESSAGE.getId(),
					group.getMember(userId).getLastCheckForMessages(),
					group));
			model.setSendTimestamp(eventRepository.findEarliestSendTimeOfEventsByTypeOfRecipient(
					EventTypes.USER_MESSAGE.getId(),
					group));
			rooms.add(model);
		}

		Collections.sort(rooms, (o1, o2) -> compareSendTimestamps(o2.getSendTimestamp(), o1.getSendTimestamp()));
		return rooms;
	}

	private int compareSendTimestamps(LocalDateTime date1, LocalDateTime date2) {
		if (date1 != null && date2 != null) {
			return date1.compareTo(date2);
		} else {
			if (date1 == null && date2 == null) {
				return 0;
			} else {
				return date1 != null ? 1 : -1;
			}
		}
	}

	// TODO: Limit für nachrichten!
	public List<ChatroomMessageModel> loadMessagesForChatroom(Long userId, Long chatroomId) {
		EventRecipient recipientGroup = eventRecipientRepository.findOne(chatroomId);
		List<ChatroomMessageModel> messages = new ArrayList<>();
		Page<Event> eventsPage = eventRepository.findByTypeAndRecipientsInOrderBySendTimeDesc(
				EventTypes.USER_MESSAGE.getId(), recipientGroup, new PageRequest(0, 1000));
		ChatroomMessageModel model = null;
		LocalDateTime latestMessageDate = null;
		for (Event event : eventsPage) {
			model = new ChatroomMessageModel();
			model.setSendTimestamp(event.getSendTime());
			model.setSender(event.getSender().getName());
			model.setText(event.getPayload());

			messages.add(model);
			if (latestMessageDate == null) {
				// sendTime der neuesten nachricht dieses chats
				latestMessageDate = event.getSendTime();
			}
		}
		if (latestMessageDate != null) {
			updateLastCheckForMessagesForChatroom(latestMessageDate, userId, chatroomId);
		}

		return messages;
	}


	public static String normalizeDialogGroupName(User user1, User user2) {
		String pattern = ">%s<>%s<";
		String u1String = (user1.getForename() + " " + user1.getSurname()).replaceAll("\n", "");
		String u2String = (user2.getForename() + " " + user2.getSurname()).replaceAll("\n", "");
		int compareValue = u1String.compareTo(u2String);
		if (compareValue < 0) {
			return String.format(pattern, u1String, u2String);
		} else {
			return String.format(pattern, u2String, u1String);
		}
	}

	public static String denormalizeDialogGroupName(String dialogGroupName, User user) {
		if (user != null) {
			Pattern pattern = Pattern.compile("\\>(.+)\\<\\>(.+)\\<");
			Matcher m = pattern.matcher(dialogGroupName);
			if (m.find()) {
				String[] result = new String[2];
				result[0] = m.group(1);
				result[1] = m.group(2);
				if (user.getForename().equals(result[0])) {
					return result[1];
				} else if (user.getForename().equals(result[1])) {
					return result[0];
				} else {
					return dialogGroupName;
				}
			}
		}
		return dialogGroupName;
	}

	public void updateLastLoginDate(Long userId) {
		User user = userRepo.findOne(userId);
		user.setLastLogin(LocalDateTime.now());
		userRepo.save(user);
	}

	public void updateLastCheckForMessagesForChatroom(LocalDateTime date, Long userId, Long roomId) {
		if (userId.equals(roomId)) {
			User user = userRepo.findOne(userId);
			user.setLastCheckForMessages(date);
			userRepo.save(user);
		} else {
			userGroupRepo.updateMembersLastCheckDatte(roomId, userId, date);
		}
	}

	public void storeChatroomNewMessageNotificationActivationStatus(Long userId, Long roomId, boolean notificationsActive) {
    	userGroupRepo.updateMembersNotificationStatus(roomId, userId, notificationsActive);
	}

	public void deleteChatroom(Long userId, Long roomId) {
		UserGroup group = userGroupRepo.findOne(roomId);
		if (group != null) {
			group.getMembers().remove(group.getMember(userId));
			if (group.getMembers().size() == 0) {
				userGroupRepo.delete(group);
			} else {
				userGroupRepo.save(group);
			}
		}
	}
}
