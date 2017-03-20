package de.dema.pd3.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import de.dema.pd3.TestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.properties")
public class TopicRepositoryTest {

	@Autowired
	private TopicRepository topicRepo;

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private VoteRepository voteRepo;
	
	private User user;
	
	@PostConstruct
	public void setup() {
		user = userRepo.save(TestUtil.createRandomUser());
		for (int i = 0; i < 12; i++) {
			Topic topic = TestUtil.createRandomTopic(user);
			topic.setDeadline(LocalDateTime.now().plusWeeks(i - 5).plusMinutes(10));
			topic.setCreationDate(LocalDateTime.now().plusWeeks(i - 11).minusMinutes(10));
			topic = topicRepo.save(topic);
			if (i % 5 == 0) {
				Vote vote = new Vote(user, topic);
				vote.setVoteTimestamp(LocalDateTime.now().minusHours(i));
				voteRepo.save(vote);
			}
		}
		User anotherUser = userRepo.save(TestUtil.createRandomUser());
		for (int i = 0; i < 6; i++) {
			Topic topic = TestUtil.createRandomTopic(anotherUser);
			topic.setDeadline(LocalDateTime.now().plusWeeks(i - 2).plusMinutes(10));
			topic.setCreationDate(LocalDateTime.now().plusWeeks(i - 6).minusMinutes(10));
			topicRepo.save(topic);
			if (i == 4) {
				Vote vote = new Vote(user, topic);
				vote.setVoteTimestamp(LocalDateTime.now().minusHours(i));
				voteRepo.save(vote);
			}
		}
	}
	
	@PreDestroy
	public void shutdown() {
		topicRepo.delete(topicRepo.findByAuthor(user, null));
		userRepo.delete(user);
	}
	
	@Test
	public void testFindAllWhereDeadlineGreaterNowAndUserHasntVotedYet() {
		Page<Topic> page = topicRepo.findAllWhereDeadlineGreaterNowAndUserHasntVotedYet(user, new PageRequest(0, 2));

		assertThat(page).isNotNull();
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getSize()).isEqualTo(2);
		assertThat(page.getTotalElements()).isEqualTo(8);
		assertThat(page.getTotalPages()).isEqualTo(4);
	}
	
}
