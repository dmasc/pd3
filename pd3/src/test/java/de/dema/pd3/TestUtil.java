package de.dema.pd3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import de.dema.pd3.persistence.Comment;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVote;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.User;

public class TestUtil {

	private static Random r = new Random();
	
	public static User createRandomUser() {
		User user = new User();
		user.setBirthday(LocalDate.now().minusYears(r.nextInt(80) + 18).minusMonths(r.nextInt(12)).minusDays(r.nextInt(28)));
		user.setDistrict(RandomStringUtils.randomAlphabetic(r.nextInt(20) + 3));
		user.setEmail(RandomStringUtils.randomAlphabetic(r.nextInt(15) + 3) + "@mail.de");
		user.setForename(RandomStringUtils.randomAlphabetic(r.nextInt(10) + 3));
		user.setIdCardNumber("T" + (r.nextInt(900000000) + 100000000));
		user.setPassword("");
		user.setPhone("0" + (r.nextInt(900) + 100)  + "-" + (r.nextInt(9000000) + 1000000));
		user.setStreet(createRandomText(r.nextInt(20) + 6) + " " + (r.nextInt(200) + 1));
		user.setSurname(RandomStringUtils.randomAlphabetic(r.nextInt(15) + 3));
		user.setZip(String.valueOf(r.nextInt(90000) + 10000));

		return user;
	}
	
	public static Topic createRandomTopic(User author) {
		Topic topic = new Topic();
		topic.setAuthor(author);
		topic.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(365)).minusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		topic.setDeadline(LocalDateTime.now().plusDays(r.nextInt(182)).plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60)));
		topic.setDescription(createRandomText(r.nextInt(5000) + 100));
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

	public static void createRandomComments(Topic topic, User author, int counter, int level, CommentRepository commentRepo, Comment parent) {
		int commentsCount = r.nextInt(counter - 1) + 1;
		for (int i = 0; i < commentsCount; i++) {
			Comment comment = createRandomComment(topic, author, parent);
			comment = commentRepo.save(comment);

			if (level > 0) {
				createRandomComments(topic, author, counter, r.nextInt(level), commentRepo, comment);
			}
		}
	}

	public static Comment createRandomComment(Topic topic, User author, Comment parent) {
		Comment comment = new Comment();
		comment.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(120)).plusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		comment.setText(createRandomText(r.nextInt(290) + 10));
		comment.setTopic(topic);
		comment.setAuthor(author);
		comment.setParent(parent);
		return comment;
	}

	public static CommentVote createRandomCommentVote(User user, Comment comment) {
		CommentVote vote = new CommentVote();
		vote.setComment(comment);
		vote.setUser(user);
		vote.setVoteTimestamp(LocalDateTime.now().minusDays(r.nextInt(90)).plusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		vote.setSelectedOption(r.nextBoolean() ? VoteOption.ACCEPTED : VoteOption.REJECTED);

		return vote;
	}
}
