package de.dema.pd3.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import de.dema.pd3.Clock;
import de.dema.pd3.FullContextTestBase;
import de.dema.pd3.TestUtil;

public class TopicRepositoryTest extends FullContextTestBase {

	private User user;
	
	@Before
	public void setup() {
		user = TestUtil.createRandomUser(this);
		for (int i = 0; i < 12; i++) {
			Topic topic = TestUtil.createRandomTopic(user);
			topic.setDeadline(Clock.now().plusWeeks(i - 5).plusMinutes(10));
			topic.setCreationDate(Clock.now().plusWeeks(i - 11).minusMinutes(10));
			topic = topicRepo.save(topic);
			if (i % 5 == 0) {
				createAndStoreTopicVote(i, topic);
			}
		}
		User anotherUser = TestUtil.createRandomUser(this);
		for (int i = 0; i < 6; i++) {
			Topic topic = TestUtil.createRandomTopic(anotherUser);
			topic.setDeadline(Clock.now().plusWeeks(i - 2).plusMinutes(10));
			topic.setCreationDate(Clock.now().plusWeeks(i - 6).minusMinutes(10));
			topicRepo.save(topic);
			if (i == 4) {
				createAndStoreTopicVote(i, topic);
			}
		}
	}

	private void createAndStoreTopicVote(int i, Topic topic) {
		TopicVote vote = new TopicVote();
		vote.setUser(user);
		vote.setTopic(topic);
		vote.setVoteTimestamp(Clock.now().minusHours(i));
		voteRepo.save(vote);
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
