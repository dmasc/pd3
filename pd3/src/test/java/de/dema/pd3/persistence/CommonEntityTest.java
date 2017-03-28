package de.dema.pd3.persistence;

import de.dema.pd3.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void testCommentDeletionAlsoRemovesChildCommentsAndLikes() {
        User user = givenRandomUserIsRegistered();

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
