package de.dema.pd3;

import de.dema.pd3.persistence.Comment;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class TestDataCreator {

	private static final Logger log = LoggerFactory.getLogger(TestDataCreator.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CommentRepository commentRepo;
	
	@Autowired
	private TopicRepository topicRepo;
	
	private Random r = new Random();
	
	@PostConstruct
	public void createData() {
		log.info("creating test data...");
		User author = new User();
		author.setBirthday(LocalDate.now().minusYears(57).plusMonths(4));
		author.setDistrict("Hamburg");
		author.setEmail("a");
		author.setForename("Franz");
		author.setIdCardNumber("T220001293");
		author.setPassword("");
		author.setPhone("0171-1234567");
		author.setStreet("Herbert-Weichmann-Straße 117");
		author.setSurname("Remmenscheid");
		author.setZip("21709");
		userRepo.save(author);

		for (int i = 0; i < 25; i++) {
			Topic topic = createTopic(author);
			topic = topicRepo.save(topic);
			createComments(topic, author, 3, 4, null);
		}
		log.info("creating test data finished");
	}

	private Topic createTopic(User author) {
		Topic topic = new Topic();
		topic.setAuthor(author);
		topic.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(365)).minusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		topic.setDeadline(LocalDateTime.now().plusDays(r.nextInt(182)).plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60)));
		topic.setDescription(createRandomText(500));
		topic.setTitle(createRandomText(r.nextInt(140) + 20));
		return topic;
	}
	
	private String createRandomText(int length) {
		String s = "";
		do {
			s += RandomStringUtils.randomAlphabetic(r.nextInt(19) + 1);
			if (r.nextInt(5) == 4) {
				s += ".";
			}
			s += " ";
		} while (s.length() < length);
		if (s.length() > length) {
			s = s.substring(0, length);
		}
		return s;
	}

	private void createComments(Topic topic, User author, int counter, int level, Comment parent) {
		int commentsCount = r.nextInt(counter - 1) + 1;
		for (int i = 0; i < commentsCount; i++) {
			Comment comment = new Comment();
			comment.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(120)).plusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
			comment.setText(createRandomText(r.nextInt(290) + 10));
			comment.setTopic(topic);
			comment.setAuthor(author);
			comment.setParent(parent);
			comment = commentRepo.save(comment);
			
			if (level > 0) {
				createComments(topic, author, counter, r.nextInt(level), comment);
			}
		}
	}
}
