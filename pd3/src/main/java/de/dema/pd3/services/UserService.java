package de.dema.pd3.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.dema.pd3.Pd3Util;
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
		log.debug("registering user [model:{}]", userModel);
		
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
		log.debug("sending message to user [senderId:{}] [recipientId:{}] [text:{}]", senderId, recipientId, text);
		Chatroom room = chatroomRepo.findBilateralRoom(senderId, recipientId);
		if (room == null) {
			room = new Chatroom();
			room.setUsers(new HashSet<>(2));
			room.getUsers().add(new ChatroomUser(userRepo.findOne(senderId), room));
			room.getUsers().add(new ChatroomUser(userRepo.findOne(recipientId), room));
			chatroomRepo.save(room);
		}
		sendMessageToChatroom(text, senderId, room.getId());
	}
	
	public List<ChatroomModel> loadAllChatroomsOrderedByTimestampOfLastMessageDesc(Long userId) {
		log.debug("loading all chatrooms [userId:{}]", userId);
		return userRepo.findOne(userId).getChatroomUsers().stream()
			.map(chatroomUser -> ChatroomModel.map(chatroomUser, c -> {
				return chatroomRepo.countNewMessages(chatroomUser.getChatroom().getId(), userId);
			}))
			.sorted().collect(Collectors.toList());
	}
	
	public Page<ChatroomMessageModel> loadMessagesForChatroom(Long userId, Long chatroomId, Long lastMsgId) {
		log.debug("loading messages for chatroom [userId:{}] [chatroomId:{}] [lastMsgId:{}]", userId, chatroomId, lastMsgId);
		
		// verify that the chatroom exists and user is allowed to see its messages
		Chatroom room = chatroomRepo.findOne(chatroomId);
		if (room == null || chatroomUserRepo.findOne(new ChatroomUserId(room, userRepo.findOne(userId))) == null) {
			return null;
		}
		
		Page<Message> findResult;
		PageRequest pageable = new PageRequest(0, 20);
		if (lastMsgId == null) {
			findResult = messageRepo.findByRoomIdOrderBySendTimestampDesc(chatroomId, pageable);
		} else {
			Message referenceMessage = messageRepo.findOne(lastMsgId);
			if (referenceMessage != null) {
				findResult = messageRepo.findByRoomIdAndOlderThanParticularMessage(chatroomId, referenceMessage.getSendTimestamp(), pageable);
			} else {
				log.warn("unable to find message while loading more messages for chatroom [userId:{}] [chatroomId:{}] [lastMsgId:{}]", userId, chatroomId, lastMsgId);
				findResult = new PageImpl<>(Collections.emptyList());
			}
		}
		return findResult.map(ChatroomMessageModel::map);
	}

	public boolean storeLastMessageRead(Long userId, Long chatroomId) {
		ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(chatroomRepo.findOne(chatroomId), userRepo.findOne(userId)));
		if (chatroomUser != null) {
			chatroomUser.setLastMessageRead(LocalDateTime.now());
			chatroomUserRepo.save(chatroomUser);
			return true;
		} else {
			log.warn("unable to find ChatroomUser entity [userId:{}] [chatroomId:{}]", userId, chatroomId);
		}
		return false;
	}
	
	public void sendMessageToChatroom(String text, Long senderId, Long chatroomId) {
		log.debug("sending message to chatroom [senderId:{}] [chatroomId:{}] [text:{}]", senderId, chatroomId, text);
		LocalDateTime now = LocalDateTime.now();

		Chatroom chatroom = chatroomRepo.findOne(chatroomId);
		LocalDateTime previousMsgSent = chatroom.getLastMessageSent();
		chatroom.setLastMessageSent(now);
		chatroom = chatroomRepo.save(chatroom);

		User sender = userRepo.findOne(senderId);
		Message msg = new Message();
		msg.setRoom(chatroom);
		msg.setSender(sender);
		msg.setSendTimestamp(now);
		msg.setText(text);
		msg = messageRepo.save(msg);
		
		ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(chatroomRepo.findOne(chatroomId), sender));
		if (chatroomUser.getLastMessageRead() != null && !chatroomUser.getLastMessageRead().isBefore(previousMsgSent)) {
			chatroomUser.setLastMessageRead(now);
			chatroomUser = chatroomUserRepo.save(chatroomUser);
		}
	}

	public void updateLastLoginDate(Long userId) {
		log.debug("updating last login date [userId:{}]", userId);
		User user = userRepo.findOne(userId);
		user.setLastLogin(LocalDateTime.now());
		userRepo.save(user);
	}

	public void storeChatroomNewMessageNotificationActivationStatus(Long userId, Long roomId, boolean notificationsActive) {
		log.debug("updating notification activation status for chatroom [userId:{}] [roomId:{}] [notificationsActive:{}]", userId, roomId, notificationsActive);
		ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(chatroomRepo.findOne(roomId), userRepo.findOne(userId)));
		chatroomUser.setNotificationsActive(notificationsActive);
		chatroomUser = chatroomUserRepo.save(chatroomUser);		
	}

	public void deleteChatroom(Long userId, Long roomId) {
		if (chatroomUserRepo.countByIdChatroomId(roomId) > 1) {
			log.debug("deleting user association with chatroom [userId:{}] [roomId:{}]", userId, roomId);
			chatroomUserRepo.delete(new ChatroomUserId(chatroomRepo.findOne(roomId), userRepo.findOne(userId)));
		} else {
			log.debug("deleting chatroom after last associated user left [userId:{}] [roomId:{}]", userId, roomId);
			chatroomRepo.delete(roomId);
		}
	}

	public List<NamedIdModel> findByQuery(String query) {
		log.debug("finding users [query:{}]", query);
		List<User> result = userRepo.findByQuery("%" + query + "%");

		if (result != null) {
			return result.stream().map(NamedIdModel::map).collect(Collectors.toList());
		}
		return null;
	}

	public boolean areNewMessagesAvailable(Long userId) {
		log.debug("checking if new messages are available [userId:{}]", userId);
		User user = userRepo.findOne(userId);
		return user.getChatroomUsers().parallelStream()
				.filter(cu -> cu.isNotificationsActive() && (cu.getLastMessageRead() == null || cu.getLastMessageRead().isBefore(cu.getChatroom().getLastMessageSent())))
				.findAny().isPresent();
	}

	public List<NamedIdModel> loadMembersOfChatroom(Long userId, Long roomId) {
		Chatroom room = chatroomRepo.findOne(roomId);
		return room.getUsers().stream()
				.filter(cu -> !cu.getUser().getId().equals(userId))
				.map(cu -> new NamedIdModel(cu.getUser().getId(), Pd3Util.username(cu.getUser())))
				.sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
				.collect(Collectors.toList());
	}

	public boolean renameChatroom(Long userId, Long roomId, String name) {
		Chatroom room = chatroomRepo.findOne(roomId);
		if (room != null) {
			ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(room, userRepo.findOne(userId)));
			if (chatroomUser != null) {
				chatroomUser.setName(StringUtils.isBlank(name) ? null : name);
				chatroomUserRepo.save(chatroomUser);
				return true;
			} else {
				log.warn("cannot find user to chatroom association [userId:{}] [roomId:{}]", userId, roomId);
			}
		} else {
			log.warn("cannot find chatroom [roomId:{}]", roomId);
		}
		return false;
	}
	
}
