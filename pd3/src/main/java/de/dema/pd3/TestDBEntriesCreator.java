package de.dema.pd3;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.dema.pd3.persistence.Comment;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@Component
public class TestDBEntriesCreator {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CommentRepository commentRepo;
	
	@Autowired
	private TopicRepository topicRepo;
	
	@PostConstruct
	public void createDate() {
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
		
		Topic topic = new Topic();
		topic.setAuthor(author);
		topic.setCreationDate(LocalDateTime.now());
		topic.setDeadline(LocalDateTime.now().plusDays(50L));
		topic.setDescription("Topic Description");
		topic.setTitle("Topic Title");
		topicRepo.save(topic);
		
		Comment comment = createComment(topic, author, "A");
		Comment a = commentRepo.save(comment);
		
		comment = createComment(topic, author, "B");
		commentRepo.save(comment);
		
		comment = createComment(topic, author, "A_A");
		comment.setParent(a);
		Comment aa = commentRepo.save(comment);
		
		comment = createComment(topic, author, "A_A_A");
		comment.setParent(aa);
		commentRepo.save(comment);

		comment = createComment(topic, author, "A_B");
		comment.setParent(a);
		commentRepo.save(comment);
		
		comment = commentRepo.findOne(3L);
		System.out.println("Replies: " + comment.getReplies());
		System.out.println("Text: " + comment.getText());
	}

	private Comment createComment(Topic topic, User author, String text) {
		Comment comment = new Comment();
		comment.setCreationDate(LocalDateTime.now());
		comment.setText(text + " Comment");
		comment.setTopic(topic);
		comment.setAuthor(author);
		return comment;
	}
}
