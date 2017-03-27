package de.dema.pd3.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.dema.pd3.TestDataCreator;
import de.dema.pd3.model.ChatroomMessageModel;
import de.dema.pd3.model.ChatroomModel;
import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
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
	
	public void sendMessage(String text, Long senderId, Long... recipient) {
		//TODO RONNY, EINBAUEN!!!
	}
	
	public List<ChatroomModel> loadAllChatroomsOrderedByTimestampOfLastMessageDesc(Long userId) {
		// TESTDATEN
		Random r = new Random();
		ArrayList<ChatroomModel> list = new ArrayList<>();
		int count = r.nextInt(19) + 1;
		for (int i = 0; i < count; i++) {
			ChatroomModel model = new ChatroomModel();
			model.setId(Long.valueOf(i));
			model.setName(TestDataCreator.createRandomText(r.nextInt(17) + 3));
			model.setSendTimestamp(LocalDateTime.now().minusHours(i + 1).minusMinutes(r.nextInt(60)));
			model.setUnreadMessagesCount(r.nextBoolean() ? r.nextInt(50) : 0);
			list.add(model);
		}
		return list;
	}
	
	public List<ChatroomMessageModel> loadMessagesForChatroom(Long userId, Long chatroomId) {
		// TESTDATEN
		Random r = new Random();
		ArrayList<ChatroomMessageModel> list = new ArrayList<>();
		int count = r.nextInt(19) + 1;
		for (int i = 0; i < count; i++) {
			ChatroomMessageModel model = new ChatroomMessageModel();
			model.setSender(TestDataCreator.createRandomText(r.nextInt(14) + 6));
			model.setSenderId(Long.valueOf(r.nextInt(2) + 1));
			model.setSendTimestamp(LocalDateTime.now().minusHours(i + 1).minusMinutes(r.nextInt(60)));
			model.setText(TestDataCreator.createRandomText(r.nextInt(300) + 3));
			list.add(model);
		}
		return list;
	}
	
	public void sendMessageToChatroom(String text, Long senderId, Long chatroomId) {
		//TODO RONNY, EINBAUEN!!!
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
