package de.dema.pd3.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.dema.pd3.Clock;
import de.dema.pd3.VoteOption;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVoteRepository;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.TopicVote;
import de.dema.pd3.persistence.TopicVoteRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class VoteServiceTest {

    @Mock
    private TopicVoteRepository topicVoteRepo;

    @Mock
    private CommentVoteRepository commentVoteRepo;

    @Mock
    private TopicRepository topicRepo;

    @Mock
    private CommentRepository commentRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private User user;
    
    @InjectMocks
	private VoteService service = new VoteService();
    
    @Captor
    private ArgumentCaptor<TopicVote> topicVoteCaptor;

	@Before
	public void init() {
		when(userRepo.findOne(anyLong())).thenReturn(user);
	}
	
	@Test
	public void testStoreTopicVoteDoesNotStoreVoteForExpiredTopic() {
		Long userId = 543L;
		Long topicId = 15L;
		VoteOption selectedOption = VoteOption.REJECTED;
		
		Topic topic = mock(Topic.class);
		when(topicRepo.findOne(anyLong())).thenReturn(topic);
		when(topic.getDeadline()).thenReturn(Clock.now().minusSeconds(1));
		
		service.storeTopicVote(userId, topicId, selectedOption);
		
		verify(topicVoteRepo, never()).findByUserIdAndTopicId(anyLong(), anyLong());
		verify(topicVoteRepo, never()).save(any(TopicVote.class));
	}

	@Test
	public void testStoreTopicVote() {
		Long userId = 543L;
		Long topicId = 15L;
		VoteOption selectedOption = VoteOption.REJECTED;
		
		when(user.getId()).thenReturn(userId);
		Topic topic = mock(Topic.class);
		when(topicRepo.findOne(anyLong())).thenReturn(topic);
		when(topic.getId()).thenReturn(topicId);
		when(topic.getDeadline()).thenReturn(Clock.now().plusSeconds(5));
		
		service.storeTopicVote(userId, topicId, selectedOption);

		verify(topicVoteRepo).findByUserIdAndTopicId(eq(userId), eq(topicId));
		verify(topicVoteRepo).save(topicVoteCaptor.capture());
		TopicVote vote = topicVoteCaptor.getValue();
		assertThat(vote.getId()).isNull();
		assertThat(vote.getUser()).isEqualTo(user);
		assertThat(vote.getTopic()).isEqualTo(topic);
		assertThat(vote.getSelectedOption()).isEqualTo(selectedOption);
		assertThat(vote.getVoteTimestamp()).isBetween(Clock.now().minusSeconds(2), Clock.now());
	}
	
}
