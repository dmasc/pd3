package de.dema.pd3.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.dema.pd3.model.ChatroomMessageModel;
import de.dema.pd3.model.ChatroomModel;
import de.dema.pd3.model.NamedIdModel;
import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.persistence.Chatroom;
import de.dema.pd3.persistence.ChatroomRepository;
import de.dema.pd3.persistence.ChatroomUser;
import de.dema.pd3.persistence.ChatroomUserId;
import de.dema.pd3.persistence.ChatroomUserRepository;
import de.dema.pd3.persistence.Message;
import de.dema.pd3.persistence.MessageRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ChatroomRepository chatroomRepo;
	
	@Autowired
	private ChatroomUserRepository chatroomUserRepo;
	
	@Autowired
	private MessageRepository messageRepo;
	
	public User registerUser(RegisterUserModel userModel) {
		log.debug("registerUser called [model:{}]", userModel);
		
		User user = new User();
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
		
		user = userRepo.save(user);
		log.info("user registered [id:{}]", user.getId());
		return user;
	}
	
	public RegisterUserModel findRegisterUserById(Long id) {
		return RegisterUserModel.map(userRepo.findOne(id));
	}
	
	public void sendMessage(String text, Long senderId, Long recipientId) {
		Chatroom room = chatroomRepo.findBilateralRoom(senderId, recipientId);
		if (room == null) {
			room = new Chatroom();
			room.setUsers(new HashSet<>(2));
			ChatroomUser chatroomUser = new ChatroomUser();
			chatroomUser.setId(room, userRepo.findOne(senderId));
			room.getUsers().add(chatroomUser);
			chatroomUser = new ChatroomUser();
			chatroomUser.setId(room, userRepo.findOne(recipientId));
			room.getUsers().add(chatroomUser);
			chatroomRepo.save(room);
		}
		sendMessageToChatroom(text, senderId, room.getId());
	}
	
	public List<ChatroomModel> loadAllChatroomsOrderedByTimestampOfLastMessageDesc(Long userId) {
		return userRepo.findOne(userId).getChatroomUsers().stream()
//			.filter(cu -> cu.isNotificationActive() && (cu.getLastMessageRead() == null || cu.getLastMessageRead().isBefore(cu.getChatroom().getLastMessageSent())))
			.map(chatroomUser -> ChatroomModel.map(chatroomUser, c -> {
				return chatroomRepo.countNewMessages(chatroomUser.getChatroom().getId(), userId);
			}))
			.sorted().collect(Collectors.toList());
	}
	
	public Page<ChatroomMessageModel> loadMessagesForChatroom(Long userId, Long chatroomId, Pageable pageable) {
		Page<ChatroomMessageModel> page = messageRepo.findByRoomIdOrderBySendTimestampDesc(chatroomId, pageable).map(ChatroomMessageModel::map);
		if (pageable.getPageNumber() == 0) {
			ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(chatroomRepo.findOne(chatroomId), userRepo.findOne(userId)));
			chatroomUser.setLastMessageRead(LocalDateTime.now());
			chatroomUserRepo.save(chatroomUser);
		}
		return page;
	}
	
	public void sendMessageToChatroom(String text, Long senderId, Long chatroomId) {
		LocalDateTime now = LocalDateTime.now();

		Chatroom chatroom = chatroomRepo.findOne(chatroomId);
		chatroom.setLastMessageSent(now);
		chatroom = chatroomRepo.save(chatroom);

		Message msg = new Message();
		msg.setRoom(chatroom);
		msg.setSender(userRepo.findOne(senderId));
		msg.setSendTimestamp(now);
		msg.setText(text);
		msg = messageRepo.save(msg);
		
		ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(
				chatroomRepo.findOne(chatroomId), 
				userRepo.findOne(senderId)
		));
		chatroomUser.setLastMessageRead(now);
		chatroomUser = chatroomUserRepo.save(chatroomUser);		
	}

	public void updateLastLoginDate(Long userId) {
		User user = userRepo.findOne(userId);
		user.setLastLogin(LocalDateTime.now());
		userRepo.save(user);
	}

	public void storeChatroomNewMessageNotificationActivationStatus(Long userId, Long roomId, boolean notificationsActive) {
		ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(
				chatroomRepo.findOne(roomId), 
				userRepo.findOne(userId)
		));
		chatroomUser.setNotificationsActive(notificationsActive);
		chatroomUser = chatroomUserRepo.save(chatroomUser);		
	}

	public void deleteChatroom(Long userId, Long roomId) {
		if (chatroomUserRepo.countByIdChatroomId(roomId) > 1) {
			chatroomUserRepo.delete(new ChatroomUserId(
					chatroomRepo.findOne(roomId), 
					userRepo.findOne(userId)
			));
		} else {
			chatroomRepo.delete(roomId);
		}
	}

	public List<NamedIdModel> findByQuery(String query) {
		List<User> result = userRepo.findByQuery("%" + query + "%");

		if (result != null) {
			return result.stream().map(NamedIdModel::map).collect(Collectors.toList());
		}
		return null;
	}

	public boolean areNewMessagesAvailable(Long userId) {
		User user = userRepo.findOne(userId);
		return user.getChatroomUsers().parallelStream()
				.filter(cu -> cu.isNotificationsActive() && (cu.getLastMessageRead() == null || cu.getLastMessageRead().isBefore(cu.getChatroom().getLastMessageSent())))
				.findAny().isPresent();
	}
	
}
