package de.dema.pd3.services;

import java.time.LocalDateTime;
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
	private EventService eventService;
	
	public User registerUser(RegisterUserModel userModel) {
		log.debug("registerUser called [model:{}]", userModel);
		
		User user = new User();
		user.setName(userModel.getForename() + " " + userModel.getSurname());
		user.setBirthday(userModel.getBirthday());
		user.setDistrict(userModel.getDistrict());
		user.setEmail(userModel.getEmail());
		user.setForename(userModel.getForename());
		user.setIdCardNumber(userModel.getIdCardNumber());
		user.setPassword(passwordEncoder.encode(userModel.getPassword()));
		user.setPhone(userModel.getPhone());
		user.setStreet(userModel.getStreet());
		user.setSurname(userModel.getSurname());
		user.setZip(userModel.getZip());
		
		user = userRepo.save(user);
		log.info("user registered [id:{}]", user.getId());
		return user;
	}
	
	public RegisterUserModel findRegisterUserById(Long id) {
		return RegisterUserModel.map(userRepo.findOne(id));
	}
	
	public void sendMessage(String text, Long senderId, Long... recipient) throws Exception {

		// senderId auflösen

		eventService.sendEvent(EventModelFactory.createUserMessage(String.valueOf(senderId), text, recipient));
	}
	
	public List<ChatroomModel> loadAllChatroomsOrderedByTimestampOfLastMessageDesc(Long userId) {
		List<ChatroomModel> rooms = new ArrayList<>();
		User user = userRepo.findOne(userId);
		List<UserGroup> groups = userGroupRepo.findByMembersIn(user);

		// one room for private messages
		ChatroomModel model = new ChatroomModel();
		model.setId(userId);
		model.setName("Private Nachrichten");
		model.setUnreadMessagesCount(0 /* TODO: magic...*/);
		model.setSendTimestamp(LocalDateTime.now() /*nochmal klären...*/);
		rooms.add(model);

		for (UserGroup group : groups) {
			model = new ChatroomModel();
			model.setId(group.getId());
			model.setName(group.getName());
			model.setUnreadMessagesCount(0 /* TODO: magic...*/);
			model.setSendTimestamp(LocalDateTime.now() /*nochmal klären...*/);
			rooms.add(model);
		}

		return rooms;
	}
	
	public List<ChatroomMessageModel> loadMessagesforChatroom(Pageable page, Long userId, Long chatroomId) {
		List<ChatroomMessageModel> messages = new ArrayList<>();
		List<EventModel> events = eventService.getEventsFor(page, chatroomId, EventTypes.USER_MESSAGE.getId());
		ChatroomMessageModel model;
		for (EventModel event : events) {
			model = new ChatroomMessageModel();
			model.setSendTimestamp(event.getTimestamp());
			model.setSender(event.getSender());
			model.setText(event.getPayload());

			messages.add(model);
		}

		//TODO Bedenke, dass auch LAST_MSG_READ_TIMESTAMP geupdatet werden muss!

		return messages;
	}
	
}
