package de.dema.pd3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import de.dema.pd3.persistence.Chatroom;
import de.dema.pd3.persistence.ChatroomRepository;
import de.dema.pd3.persistence.ChatroomUser;
import de.dema.pd3.persistence.Comment;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVote;
import de.dema.pd3.persistence.CommentVoteRepository;
import de.dema.pd3.persistence.Message;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.TopicVote;
import de.dema.pd3.persistence.TopicVoteRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;
import de.dema.pd3.services.UserService;

@Controller
@RequestMapping("/test")
public class TestDataController {

	private static final Logger log = LoggerFactory.getLogger(TestDataController.class);

	private static Random r = new Random();	

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CommentRepository commentRepo;
	
	@Autowired
	private TopicRepository topicRepo;
	
	@Autowired
	private TopicVoteRepository topicVoteRepo;
	
	@Autowired
	private CommentVoteRepository commentVoteRepo;
	
	@Autowired
	private ChatroomRepository chatroomRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserService userService;
	
	// Der Wert kann in der Run Config als VM-Parameter gesetzt werden: -Dtestdata=true
	@Value("${testdata:false}")
	private boolean shouldCreateTestData;
	
	@PostConstruct
	public void createTestData() {
		if (shouldCreateTestData) {
			log.info("creating test data...");
			User author = createDefaultTestUser();
			User authorFemale = createFemaleUser();
			createValidEmailUser();

			for (int i = 0; i < 25; i++) {
				Topic topic = createTopic(author, authorFemale);
				topic = topicRepo.save(topic);

				createComments(topic, 15, 4, null, author, authorFemale);
				
				TopicVote vote = new TopicVote();
				vote.setUser(r.nextBoolean() ? author : authorFemale);
				vote.setTopic(topic);
				vote.setVoteTimestamp(LocalDateTime.now().minusDays(r.nextInt(7)).minusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
				vote.setSelectedOption(VoteOption.values()[r.nextInt(VoteOption.values().length)]);
				topicVoteRepo.save(vote);
			}
			
			createMessages(author, authorFemale);
			
			log.info("creating test data finished");
		}
	}

	@PostMapping("/receive-message")
	@ResponseBody
	public boolean receiveMessage(Authentication auth) {
		log.debug("receiving random message triggered");
		Long currentUserId = Pd3Util.currentUserId(auth);
		
		List<User> list = new ArrayList<>();
		userRepo.findAll().forEach(user -> {
			if (!user.getId().equals(currentUserId)) {
				list.add(user);
			}
		});
		Collections.shuffle(list);
		User sender = list.remove(0);
		userService.sendMessage(createRandomText(r.nextInt(1000) + 2), sender.getId(), currentUserId);
		return true;
	}

	private User createFemaleUser() {
		User.Builder userBuilder = new User.Builder();
		LocalDate birthday = LocalDate.now().minusYears(r.nextInt(50) + 18).minusMonths(r.nextInt(12));
		userBuilder.birthday(birthday).district("Hamburg").email("jutta").forename("Jutta").idCardNumber("T210001741").female()
				.password(passwordEncoder.encode("test")).phone("040-2225256").street("Otto-von-Bismark-Allee 32").surname("Sorin-Gießmann").zip("22177");
		return userRepo.save(userBuilder.build());
	}

	private User createDefaultTestUser() {
		User.Builder userBuilder = new User.Builder();
		LocalDate birthday = LocalDate.now().minusYears(r.nextInt(50) + 18).minusMonths(r.nextInt(12));
		userBuilder.birthday(birthday).district("Hamburg").email("a").forename("Franz").idCardNumber("T220001293").male()
				.password(passwordEncoder.encode("")).phone("0171-1234567").street("Herbert-Weichmann-Straße 117").surname("Remmenscheid").zip("21709");
		return userRepo.save(userBuilder.build());
	}

	private User createValidEmailUser() {
		User.Builder userBuilder = new User.Builder();
		LocalDate birthday = LocalDate.now().minusYears(r.nextInt(50) + 18).minusMonths(r.nextInt(12));
		userBuilder.birthday(birthday).district("Hamburg").email("d_masche@hotmail.com").forename("Philip").idCardNumber("T223857534").male()
				.password(passwordEncoder.encode("test")).phone("0152-87421454").street("Hamburger Straße 37").surname("Engeljäger").zip("21239");
		return userRepo.save(userBuilder.build());
	}

	private void createMessages(User u1, User u2) {
		Chatroom room = new Chatroom();
		room.setUsers(new HashSet<>(2));
		room.getUsers().add(new ChatroomUser(u1, room));
		room.getUsers().add(new ChatroomUser(u2, room));
		
		room.setMessages(new HashSet<>());
		int msgCount = r.nextInt(10) + 41;
		LocalDateTime lastMsgSent = null; 
		for (int i = 0; i < msgCount; i++) {
			Message msg = new Message.Builder().room(room).sender(r.nextBoolean() ? u1 : u2).sendTimestamp(createRandomDateTime(-1, -50))
					.text(createRandomText(r.nextInt(1000) + 2)).build();
			if (lastMsgSent == null || lastMsgSent.isBefore(msg.getSendTimestamp())) {
				lastMsgSent = msg.getSendTimestamp();
			}
			room.getMessages().add(msg);
		}
		room.setLastMessageSent(lastMsgSent);

		chatroomRepo.save(room);
	}

	private Topic createTopic(User... author) {
		Topic topic = new Topic();
		topic.setAuthor(author[r.nextInt(author.length)]);
		topic.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(365)).minusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		topic.setDeadline(LocalDateTime.now().plusDays(r.nextInt(182)).plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60)));
		topic.setDescription(createRandomText(r.nextInt(1000) + 500));
		topic.setTitle(createRandomText(r.nextInt(140) + 20));
		return topic;
	}
	
	public static String createRandomText(int length) {
		String s = "";
		do {
			s += RandomStringUtils.randomAlphabetic(r.nextInt(19) + 1);
			if (r.nextInt(7) == 6) {
				s += ".";
			}
			s += " ";
		} while (s.length() < length);
		if (s.length() > length) {
			s = s.substring(0, length);
		}
		return s;
	}

	private void createComments(Topic topic, int counter, int level, Comment parent, User... author) {
		int commentsCount = r.nextInt(counter - 1) + 1;
		for (int i = 0; i < commentsCount; i++) {
			Comment comment = new Comment();
			comment.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(120)).plusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
			comment.setText(createRandomText(r.nextInt(490) + 10));
			comment.setTopic(topic);
			comment.setAuthor(author[r.nextInt(author.length)]);
			comment.setParent(parent);
			comment = commentRepo.save(comment);
			
			if (author.length > 1 && r.nextBoolean()) {
				User user;
				do {
					user = author[r.nextInt(author.length)];
				} while (user.equals(comment.getAuthor()));
				CommentVote vote = new CommentVote();
				vote.setComment(comment);
				vote.setUser(user);
				vote.setSelectedOption(r.nextBoolean() ? VoteOption.ACCEPTED : VoteOption.REJECTED);
				vote.setVoteTimestamp(LocalDateTime.now());
				commentVoteRepo.save(vote);
			}
			if (level > 0) {
				createComments(topic, 3, r.nextInt(level), comment, author);
			}
		}
	}

	public static LocalDateTime createRandomDateTime(int minOffsetDays, int maxOffsetDays) {
		LocalDateTime dateTime = LocalDateTime.now();
		if (minOffsetDays < 0 || maxOffsetDays < 0) {
			dateTime = dateTime.minusDays(r.nextInt(Math.abs(maxOffsetDays - minOffsetDays)) - minOffsetDays)
					.minusHours(r.nextInt(24)).minusMinutes(r.nextInt(60));
		} else {
			dateTime = dateTime.plusDays(r.nextInt(maxOffsetDays - minOffsetDays) + minOffsetDays)
					.plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60));
		}
		
		return dateTime;
	}

}
