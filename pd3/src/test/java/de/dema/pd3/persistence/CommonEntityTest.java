package de.dema.pd3.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import de.dema.pd3.TestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.properties")
public class CommonEntityTest {

    @Autowired
    private TopicRepository topicRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TopicVoteRepository voteRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private CommentVoteRepository commentVoteRepo;
    
    @Autowired
    private DataSource dataSource;
    
    private JdbcTemplate jdbc;

	@PostConstruct
	public void setup() {
		jdbc = new JdbcTemplate(dataSource);
	}
	
    @Test
    public void testCommentDeletionAlsoRemovesChildCommentsAndLikes() {
        User user = givenRandomUserIsRegistered();
        User commenter = givenRandomUserIsRegistered();
        Topic topic = givenUserCreatedTopic(user);
        Comment parentComment1 = givenUserCommentedTopic(commenter, topic);
        Comment childA = givenUserRepliedToComment(user, topic, parentComment1);
        Comment childAA = givenUserRepliedToComment(commenter, topic, childA);
        givenUserRepliedToComment(user, topic, childAA);
        givenUserRepliedToComment(commenter, topic, childAA);
        Comment childAB = givenUserRepliedToComment(commenter, topic, childA);
        Comment childABA = givenUserRepliedToComment(user, topic, childAB);
        givenUserRepliedToComment(commenter, topic, childAB);

        givenUserLikedOrDislikedComment(user, parentComment1);
        givenUserLikedOrDislikedComment(commenter, childA);
        givenUserLikedOrDislikedComment(user, childAA);
        givenUserLikedOrDislikedComment(commenter, childABA);

        Comment parentComment2 = givenUserCommentedTopic(user, topic);
        Comment childA2 = givenUserRepliedToComment(user, topic, parentComment2);
        childAA = givenUserRepliedToComment(commenter, topic, childA2);
        childAB = givenUserRepliedToComment(commenter, topic, childA2);
        givenUserRepliedToComment(user, topic, childAB);
        givenUserRepliedToComment(commenter, topic, childAB);
        givenUserRepliedToComment(user, topic, childAB);
        givenUserRepliedToComment(commenter, topic, childAB);
        
        givenUserLikedOrDislikedComment(commenter, childA2);
        givenUserLikedOrDislikedComment(user, childAA);
        givenUserLikedOrDislikedComment(user, childAB);
        
        long totalCommentsCount = commentRepo.count();
        assertThat(totalCommentsCount).isEqualTo(16L);
        
        long totalLikesCount = commentVoteRepo.count();
        assertThat(totalLikesCount).isEqualTo(7L);
        
        commentRepo.delete(childAB.getId());
        assertThat(commentRepo.count()).isEqualTo(totalCommentsCount - 5);
        assertThat(commentVoteRepo.count()).isEqualTo(totalLikesCount - 1);

        commentRepo.delete(childABA.getId());
        assertThat(commentRepo.count()).isEqualTo(totalCommentsCount - 6);
        assertThat(commentVoteRepo.count()).isEqualTo(totalLikesCount - 2);

        commentRepo.delete(childA.getId());
        assertThat(commentRepo.count()).isEqualTo(totalCommentsCount - 12);
        assertThat(commentVoteRepo.count()).isEqualTo(totalLikesCount - 4);

        commentRepo.delete(parentComment1.getId());
        commentRepo.delete(parentComment2.getId());
        assertThat(commentRepo.count()).isEqualTo(0L);
        assertThat(commentVoteRepo.count()).isEqualTo(0L);
        
        topicRepo.delete(topic.getId());
        userRepo.delete(commenter.getId());
        userRepo.delete(user.getId());
    }

    private CommentVote givenUserLikedOrDislikedComment(User commenter, Comment childAA) {
        return commentVoteRepo.save(TestUtil.createRandomCommentVote(commenter, childAA));
	}

	private User givenRandomUserIsRegistered() {
        return userRepo.save(TestUtil.createRandomUser());
    }

    private Topic givenUserCreatedTopic(User user) {
        return topicRepo.save(TestUtil.createRandomTopic(user));
    }

    private Comment givenUserCommentedTopic(User user, Topic topic) {
        return commentRepo.save(TestUtil.createRandomComment(topic, user, null));
    }

    private Comment givenUserRepliedToComment(User user, Topic topic, Comment comment) {
        return commentRepo.save(TestUtil.createRandomComment(topic, user, comment));
    }

}
