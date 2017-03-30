package de.dema.pd3;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import javax.annotation.PostConstruct;

import de.dema.pd3.persistence.*;
import de.dema.pd3.services.EventService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestDataCreator {

	private static final Logger log = LoggerFactory.getLogger(TestDataCreator.class);

	@Autowired
	private EventRepository eventRepository;

	private static Random r = new Random();

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserGroupRepository groupRepo;
	
	@Autowired
	private CommentRepository commentRepo;
	
	@Autowired
	private TopicRepository topicRepo;
	
	@Autowired
	private TopicVoteRepository topicVoteRepo;
	
	@Autowired
	private CommentVoteRepository commentVoteRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	private EventService eventService;
	
	// Der Wert kann in der Run Config als VM-Parameter gesetzt werden: -Dtestdata=true
	@Value("${testdata:false}")
	private boolean shouldCreateTestData;
	
	@PostConstruct
	public void createTestData() {
		if (shouldCreateTestData) {
			log.info("creating test data...");
			try {
				LocalDateTime now = LocalDateTime.of(2017, 1, 1, 0, 0);

				User author = new User();
				author.setName("Franz");
				author.setBirthday(LocalDate.now().minusYears(r.nextInt(50) + 18).minusMonths(r.nextInt(12)));
				author.setDistrict("Hamburg");
				author.setEmail("a");
				author.setForename("Franz");
				author.setIdCardNumber("T220001293");
				author.setPassword(passwordEncoder.encode(""));
				author.setPhone("0171-1234567");
				author.setStreet("Herbert-Weichmann-Straße 117");
				author.setSurname("Remmenscheid");
				author.setZip("21709");
				author.setMale(true);
				author.setLastCheckForMessages(now);
				userRepo.save(author);

				User authorFemale = new User();
				authorFemale.setBirthday(LocalDate.now().minusYears(r.nextInt(50) + 18).minusMonths(r.nextInt(12)));
				authorFemale.setDistrict("Hamburg");
				authorFemale.setName("Jutta");
				authorFemale.setEmail("jutta");
				authorFemale.setForename("Jutta");
				authorFemale.setIdCardNumber("T210001741");
				authorFemale.setPassword(passwordEncoder.encode("test"));
				authorFemale.setPhone("040-2225256");
				authorFemale.setStreet("Otto-von-Bismark-Allee 32");
				authorFemale.setSurname("Sorin-Gießmann");
				authorFemale.setZip("22177");
				authorFemale.setMale(false);
				authorFemale.setLastCheckForMessages(now);
				userRepo.save(authorFemale);

				UserGroup group = new UserGroup();
				group.setName("TestGroup");
				group.addMembers(author, authorFemale);
				groupRepo.save(group);

				UserGroup group2 = new UserGroup();
				group2.setName("TestGroupMale");
				group2.addMembers(author);
				groupRepo.save(group2);

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

				Event franzMsg = new Event();
				franzMsg.setPayload("Hallo Jutta!");
				franzMsg.setSender(author);
				franzMsg.setType(1);
				franzMsg.setRecipients(new HashSet<>(Arrays.asList(group)));
				franzMsg.setSendTime(LocalDateTime.now());
				eventRepository.save(franzMsg);

				Event juttaMsg = new Event();
				juttaMsg.setSender(authorFemale);
				juttaMsg.setPayload("Hallo zurück...");
				juttaMsg.setType(1);
				juttaMsg.setSendTime(LocalDateTime.now());
				juttaMsg.setRecipients(new HashSet<>(Arrays.asList(group)));
				eventRepository.save(juttaMsg);

				Event msg;
				for (int i = 0; i < 4; i++) {
					msg = new Event();
					msg.setSender(authorFemale);
					msg.setPayload("Text " + (i +1));
					msg.setType(1);
					msg.setSendTime(LocalDateTime.now());
					msg.setRecipients(new HashSet<>(Arrays.asList(group)));
					eventRepository.save(msg);
				}


				Event msgAll = new Event();
				msgAll.setSender(group);
				msgAll.setPayload("Hallo Broadcast...");
				msgAll.setType(1);
				msgAll.setSendTime(LocalDateTime.now());
				msgAll.setRecipients(new HashSet<>(Arrays.asList(group)));
				eventRepository.save(msgAll);

				log.info("creating test data finished");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
}
