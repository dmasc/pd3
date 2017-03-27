package de.dema.pd3.services;

import java.time.LocalDateTime;
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
		User receivingUser = userRepo.findOne(recipientUserId);
		UserGroup recipientGroup = userGroupRepo.findUserGroupForDialogBetweenMembers(sendingUser, receivingUser);
		if (recipientGroup == null) {
			recipientGroup = new UserGroup();
			recipientGroup.setName(sendingUser.getForename() + "<->" + receivingUser.getForename());
			recipientGroup.setCreated(LocalDateTime.now());
			List<User> members = new ArrayList<>();
			members.add(sendingUser);
			members.add(receivingUser);
			recipientGroup.setMembers(members);
			recipientGroup = userGroupRepo.save(recipientGroup);
		}
		try {
			eventService.sendEvent(
                    EventModelFactory.createUserMessage(
                            sendingUser.getForename() + " " + sendingUser.getSurname(),
                            text,
                            recipientGroup.getId()));
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
			model.setName(group.getName());
			model.setUnreadMessagesCount(eventRepository.countEventsByTypeOfRecipient(
					EventTypes.USER_MESSAGE.getId(),
					user.getLastCheckForMessages(),
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

	public List<ChatroomMessageModel> loadMessagesforChatroom(Long userId, Long chatroomId) {
		EventRecipient recipientGroup = userGroupRepo.findOne(chatroomId);
		if (recipientGroup == null) {
			recipientGroup = userRepo.findOne(userId);
		}
		List<ChatroomMessageModel> messages = new ArrayList<>();
		Page<Event> eventsPage = eventRepository.findByTypeAndRecipientsIn(
				EventTypes.USER_MESSAGE.getId(), Arrays.asList(recipientGroup), new PageRequest(0, 1000));
		ChatroomMessageModel model;
		for (Event event : eventsPage) {
			model = new ChatroomMessageModel();
			model.setSendTimestamp(event.getSendTime());
			model.setSender(event.getSender());
			model.setText(event.getPayload());

			messages.add(model);
		}

		//TODO Bedenke, dass auch LAST_MSG_READ_TIMESTAMP geupdatet werden muss!
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

	public static String[] denormalizeDialogGroupName(String dialogGroupName) {
		Pattern pattern = Pattern.compile("\\>(.+)\\<\\>(.+)\\<");
		Matcher m = pattern.matcher(dialogGroupName);
		if (m.find()) {
			String[] result = new String[2];
			result[0] = m.group(1);
			result[1] = m.group(2);
			return result;
		}
		return null;
	}

	public void updateLastLoginDate(Long userId) {
		User user = userRepo.findOne(userId);
		user.setLastLogin(LocalDateTime.now());
		userRepo.save(user);
	}

	public void storeChatroomNewMessageNotificationActivationStatus(Long userId, Long roomId, boolean notificationsActive) {
    	//TODO Änderung der Chatraum-Notification-Einstellung abspeichern.
	}

	public void deleteChatroom(Long userId, Long roomId) {
		//TODO Lösche Assoziierung (geschrieben sieht das Wort echt schräg aus) des Users mit dem Raum
		//TODO Lösche den Raum, wenn keine weiteren Assoziierungen mit Usern mehr bestehen
	}
}
