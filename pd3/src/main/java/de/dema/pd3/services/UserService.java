package de.dema.pd3.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.dema.pd3.Clock;
import de.dema.pd3.Pd3Util;
import de.dema.pd3.model.ChatroomMessageModel;
import de.dema.pd3.model.ChatroomModel;
import de.dema.pd3.model.NamedIdModel;
import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.persistence.Chatroom;
import de.dema.pd3.persistence.ChatroomRepository;
import de.dema.pd3.persistence.ChatroomUser;
import de.dema.pd3.persistence.ChatroomUser.ChatroomUserId;
import de.dema.pd3.persistence.ChatroomUserRepository;
import de.dema.pd3.persistence.Image;
import de.dema.pd3.persistence.ImageRepository;
import de.dema.pd3.persistence.Message;
import de.dema.pd3.persistence.MessageRepository;
import de.dema.pd3.persistence.PasswordResetToken;
import de.dema.pd3.persistence.PasswordResetTokenRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;
import de.dema.pd3.security.CurrentUser;
import de.dema.pd3.security.Pd3AuthenticationSuccessHandler;
import de.dema.pd3.security.Pd3UserDetailsService;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private Pd3UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ChatroomRepository chatroomRepo;

	@Autowired
	private ChatroomUserRepository chatroomUserRepo;

	@Autowired
	private MessageRepository messageRepo;

	@Autowired
	private PasswordResetTokenRepository passwordTokenRepo;

	@Autowired
	private ImageRepository imageRepo;
	
	@Autowired
	private MailSender mailSender;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private Pd3AuthenticationSuccessHandler authSuccessHandler;
	
	@Value("${spring.mail.username}")
	private String pd3MailSenderAddress;

	/**
	 * Erzeugt einen {@linkplain User}, speichert in der DB und gibt die zugewiesene ID zurück.
	 * 
	 * @param userModel Das Model mit den Daten des Benutzers, der gespeichert werden soll.
	 * @return Die ID des Benutzerdatensatzes ind er Datenbank.
	 */
	public Long registerUser(RegisterUserModel userModel) {
		log.debug("registering user [model:{}]", userModel);

		User.Builder userBuilder = new User.Builder();
		userBuilder.birthday(conversionService.convert(userModel.getBirthday(), LocalDate.class))
				.district(userModel.getDistrict()).email(userModel.getEmail()).forename(userModel.getForename())
				.idCardNumber(userModel.getIdCardNumber()).male(userModel.isMale()).password(passwordEncoder.encode(userModel.getPassword()))
				.phone(userModel.getPhone()).street(userModel.getStreet()).surname(userModel.getSurname()).zip(userModel.getZip());

		User user = userRepo.save(userBuilder.build());
		log.info("user registered [userId:{}]", user.getId());

		authenticateUser(user.getEmail());
		
		return user.getId();
	}

	public RegisterUserModel findRegisterUserById(Long id) {
		return RegisterUserModel.map(userRepo.findOne(id));
	}

	public Long storeProfilePicture(Long userId, MultipartFile file) {
		log.debug("storing user profile picture [userId:{}]", userId);

		if (!file.isEmpty()) {
			try {
				User user = userRepo.findOne(userId);
				String formatName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1).toUpperCase();
				byte[] originalImageBytes = file.getBytes();
				byte[] resizedImage = ImageService.resize(originalImageBytes, formatName, 300, 300);
				if (resizedImage != null) {
					Image image = new Image.Builder(resizedImage).owner(user).type(formatName).build();
					Image big = imageRepo.save(image);
					
					image = new Image.Builder(ImageService.resize(originalImageBytes, formatName, 50, 50)).owner(user).type(formatName).build();
					image = imageRepo.save(image);
	
					if (user.getProfilePicture() != null) {
						deleteProfilePicture(userId);
					}
					user.setProfilePicture(big);
					user.setProfilePictureSmall(image);
					userRepo.save(user);
					log.info("user profile picture stored [userId:{}]", userId);
					return image.getId();
				} else {
					log.warn("user profile picture could not be resized [userId:{}] [file:{}]", userId, file.getOriginalFilename());					
				}
			} catch (IOException e) {
				log.error("failed to store user profile image [filename:{}]", file.getOriginalFilename(), e);
			}
		} else {
			log.debug("storing user profile picture failed, uploaded file is empty [userId:{}]", userId);
		}
		return null;
	}
	
	public void deleteProfilePicture(Long userId) {
		log.debug("deleting user profile picture [userId:{}]", userId);
		
		User user = userRepo.findOne(userId);
		if (user != null && user.getProfilePicture() != null) {
			Image big = user.getProfilePicture();
			Image small = user.getProfilePictureSmall();
			user.setProfilePicture(null);
			user.setProfilePictureSmall(null);
			userRepo.save(user);
			imageRepo.delete(big);
			imageRepo.delete(small);
			log.info("user profile picture deleted [userId:{}]", userId);
		}
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
				})).sorted().collect(Collectors.toList());
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
			chatroomUser.setLastMessageRead(Clock.now());
			chatroomUserRepo.save(chatroomUser);
			return true;
		} else {
			log.warn("unable to find ChatroomUser entity [userId:{}] [chatroomId:{}]", userId, chatroomId);
		}
		return false;
	}

	public void sendMessageToChatroom(String text, Long senderId, Long chatroomId) {
		log.debug("sending message to chatroom [senderId:{}] [chatroomId:{}] [text:{}]", senderId, chatroomId, text);
		LocalDateTime now = Clock.now();

		Chatroom chatroom = chatroomRepo.findOne(chatroomId);
		LocalDateTime previousMsgSent = chatroom.getLastMessageSent();
		chatroom.setLastMessageSent(now);
		chatroom = chatroomRepo.save(chatroom);

		User sender = userRepo.findOne(senderId);
		messageRepo.save(new Message.Builder().room(chatroom).sender(sender).sendTimestamp(now).text(Pd3Util.injectHtmlTags(text)).build());

		ChatroomUser chatroomUser = chatroomUserRepo.findOne(new ChatroomUserId(chatroomRepo.findOne(chatroomId), sender));
		if (previousMsgSent == null || chatroomUser.getLastMessageRead() != null && !chatroomUser.getLastMessageRead().isBefore(previousMsgSent)) {
			chatroomUser.setLastMessageRead(now);
			chatroomUser = chatroomUserRepo.save(chatroomUser);
		}
	}

	public void updateLastLoginDate(Long userId) {
		log.debug("updating last login date [userId:{}]", userId);
		User user = userRepo.findOne(userId);
		user.setLastLogin(Clock.now());
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
		return user.getChatroomUsers() != null && user.getChatroomUsers().parallelStream()
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

	public void createAndSendPasswordResetToken(String baseUrl, String email) {
		log.debug("sending password reset token via email [baseUrl:{}] [recipient:{}]", baseUrl, email);
		User user = userRepo.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("No user found with email '" + email + "'");
		}
		String token = UUID.randomUUID().toString();
		passwordTokenRepo.save(new PasswordResetToken(passwordEncoder.encode(token), user));
		SimpleMailMessage mail = constructResetTokenEmail(baseUrl, token, user);
		mailSender.send(mail);
		log.info("password reset token sent via email [userId:{}]", user.getId());
	}

	private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, User user) {
		String url = contextPath + "/public/change-password?id=" + user.getId() + "&token=" + token;
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject("Zurücksetzen des Passworts für Ihr PD3-Benutzerkonto");
		email.setText("Klicken Sie auf den nachfolgenden Link, um Ihr Passwort zurückzusetzen:\r\n" + url);
		email.setTo(user.getEmail());
		email.setFrom(pd3MailSenderAddress);
		return email;
	}

	public boolean validatePasswordResetToken(Long userId, String token) {
		PasswordResetToken passToken = passwordTokenRepo.findByUserId(userId);
		if (passToken != null) {
			if (Clock.now().isBefore(passToken.getExpiryDate())) {
				return passwordEncoder.matches(token, passToken.getToken());
			} else {
				passwordTokenRepo.delete(passToken);
			}
		}
		return false;
	}

	public void changePassword(Long userId, String password) {
		User user = userRepo.findOne(userId);
		user.setPassword(passwordEncoder.encode(password));
		user = userRepo.save(user);
		log.info("password reset by user [userId:{}]", userId);

		deletePasswordResetToken(userId);
	    
		authenticateUser(user.getEmail());
	}

	@Transactional
	public void deletePasswordResetToken(Long userId) {
		int count = passwordTokenRepo.deleteByUserId(userId);
		if (count > 0) {
			log.info("user password token deleted [userId:{}]", userId);
		}
	}

	public void authenticateUser(String email) {
		CurrentUser user = userDetailsService.loadUserByUsername(email);
	    Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    try {
			authSuccessHandler.onAuthenticationSuccess(null, null, authentication);
			log.info("programmatically authenticated user [userId:{}]", user.getId());
		} catch (IOException | ServletException e) {
			log.error("failed to programmatically invoke authentication success handler [userId:{}]", user.getId(), e);
		}
	}
	
}
