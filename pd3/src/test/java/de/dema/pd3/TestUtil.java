package de.dema.pd3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.repository.CrudRepository;

import de.dema.pd3.persistence.Chatroom;
import de.dema.pd3.persistence.ChatroomUser;
import de.dema.pd3.persistence.Comment;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVote;
import de.dema.pd3.persistence.Message;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicVote;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.VoteBase;

public class TestUtil {

	private static Random r = new Random();
	
	public static User createRandomUser() {
		return createRandomUser(null);
	}
	
	public static User createRandomUser(RepositoryProvider repoProvider) {
		User user = new User();
		user.setBirthday(LocalDate.now().minusYears(r.nextInt(80) + 18).minusMonths(r.nextInt(12)).minusDays(r.nextInt(28)));
		user.setDistrict(RandomStringUtils.randomAlphabetic(r.nextInt(20) + 3));
		user.setEmail(RandomStringUtils.randomAlphabetic(r.nextInt(15) + 3) + "@mail.de");
		user.setForename(RandomStringUtils.randomAlphabetic(r.nextInt(10) + 3));
		user.setIdCardNumber("T" + (r.nextInt(900000000) + 100000000));
		user.setMale(r.nextBoolean());
		user.setPassword("");
		user.setPhone("0" + (r.nextInt(900) + 100)  + "-" + (r.nextInt(9000000) + 1000000));
		user.setStreet(createRandomText(r.nextInt(20) + 6) + " " + (r.nextInt(200) + 1));
		user.setSurname(RandomStringUtils.randomAlphabetic(r.nextInt(15) + 3));
		user.setZip(String.valueOf(r.nextInt(90000) + 10000));

		return repoProvider != null ? repoProvider.getUserRepository().save(user) : user;
	}
	
	public static Topic createRandomTopic(User author) {
		return createRandomTopic(null, author);
	}
	
	public static Topic createRandomTopic(RepositoryProvider repoProvider, User author) {
		Topic topic = new Topic();
		topic.setAuthor(author);
		topic.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(365)).minusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		topic.setDeadline(LocalDateTime.now().plusDays(r.nextInt(182)).plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60)));
		topic.setDescription(createRandomText(r.nextInt(5000) + 100));
		topic.setTitle(createRandomText(r.nextInt(140) + 20));
		return repoProvider != null ? repoProvider.getTopicRepository().save(topic) : topic;
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
		return createRandomComment(null, topic, author, parent);
	}

	public static Comment createRandomComment(RepositoryProvider repoProvider, Topic topic, User author, Comment parent) {
		Comment comment = new Comment();
		comment.setCreationDate(LocalDateTime.now().minusDays(r.nextInt(120)).plusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		comment.setText(createRandomText(r.nextInt(290) + 10));
		comment.setTopic(topic);
		comment.setAuthor(author);
		comment.setParent(parent);
		return repoProvider != null ? repoProvider.getCommentRepository().save(comment) : comment;
	}

	public static CommentVote createRandomCommentVote(User user, Comment comment) {
		return createRandomCommentVote(null, user, comment);
	}

	public static CommentVote createRandomCommentVote(RepositoryProvider repoProvider, User user, Comment comment) {
		CommentVote vote = new CommentVote();
		vote.setComment(comment);
		return createRandomTopicVote(repoProvider != null ? repoProvider.getCommentVoteRepository() : null, vote, user);
	}
	
	public static TopicVote createRandomTopicVote(RepositoryProvider repoProvider, User user, Topic topic) {
		TopicVote vote = new TopicVote();
		vote.setTopic(topic);
		return createRandomTopicVote(repoProvider != null ? repoProvider.getTopicVoteRepository() : null, vote, user);
	}
	
	private static <T extends VoteBase> T createRandomTopicVote(CrudRepository<T, Long> repo, T vote, User user) {
		vote.setUser(user);
		vote.setVoteTimestamp(LocalDateTime.now().minusDays(r.nextInt(90)).plusHours(r.nextInt(24)).minusMinutes(r.nextInt(60)));
		vote.setSelectedOption(r.nextBoolean() ? VoteOption.ACCEPTED : VoteOption.REJECTED);

		return repo != null ? repo.save(vote) : vote;
	}
	
	public static ChatroomUser createChatroomUser(Chatroom room, User user, boolean notificationsActive) {
		ChatroomUser chatroomUser = new ChatroomUser();
		chatroomUser.setId(room, user);
		chatroomUser.setNotificationsActive(notificationsActive);
		
		return chatroomUser;
	}

	public static Chatroom createChatroom(RepositoryProvider repoProvider, User... users) {
		Chatroom chatroom = new Chatroom();
		chatroom.setUsers(Arrays.asList(users).stream().map(u -> createChatroomUser(chatroom, u, true)).collect(Collectors.toSet()));
		
		return repoProvider.getChatroomRepository().save(chatroom);
	}
	
	public static Message createRandomMessage(Chatroom room, User sender) {
		return createRandomMessage(null, room, sender);
	}
	
	public static Message createRandomMessage(RepositoryProvider repoProvider, Chatroom room, User sender) {
		Message message = new Message();
		message.setRoom(room);
		message.setSender(sender);
		message.setSendTimestamp(createRandomDateTime(-1, -60));
		message.setText(createRandomText(r.nextInt(1000) + 2));

		return repoProvider != null ? repoProvider.getMessageRepository().save(message) : message;		
	}

	public static List<Message> createRandomMessages(RepositoryProvider repoProvider, Chatroom room, User sender, int amount) {
		List<Message> result = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			result.add(createRandomMessage(repoProvider, room, sender));
		}
		Collections.sort(result, (msg1, msg2) -> msg1.getSendTimestamp().compareTo(msg2.getSendTimestamp()));
		return result;
	}
	
	public static LocalDateTime createRandomDateTime(int minOffsetDays, int maxOffsetDays) {
		LocalDateTime dateTime = LocalDateTime.now().plusHours(r.nextInt(24)).minusMinutes(r.nextInt(60));
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
