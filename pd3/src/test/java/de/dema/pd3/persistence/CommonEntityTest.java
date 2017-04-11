package de.dema.pd3.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import de.dema.pd3.DBTestBase;
import de.dema.pd3.TestUtil;

public class CommonEntityTest extends DBTestBase {

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

    @Test
    public void testTopicDeletionAlsoRemovesCommentsAndVotes() {
    	User user = givenRandomUserIsRegistered();
    	User user2 = givenRandomUserIsRegistered();
    	Topic topic = givenUserCreatedTopic(user);
    	Comment comment = givenUserCommentedTopic(user2, topic);
    	givenUserVotedComment(user, comment);
    	givenUserRepliedToComment(user, topic, comment);
    	givenUserRepliedToComment(user2, topic, comment);
    	comment = givenUserCommentedTopic(user, topic);
    	givenUserCommentedTopic(user2, topic);
    	givenUserVotedComment(user2, comment);
    	givenUserVotedTopic(user2, topic);

        assertThat(userRepo.count()).isEqualTo(2L);
        assertThat(topicRepo.count()).isEqualTo(1L);
        assertThat(commentRepo.count()).isEqualTo(5L);
        assertThat(topicVoteRepo.count()).isEqualTo(1L);
        assertThat(commentVoteRepo.count()).isEqualTo(2L);    	
        
        topicRepo.delete(topic.getId());

        assertThat(userRepo.count()).isEqualTo(2L);
        assertThat(topicRepo.count()).isEqualTo(0L);
        assertThat(commentRepo.count()).isEqualTo(0L);
        assertThat(topicVoteRepo.count()).isEqualTo(0L);
        assertThat(commentVoteRepo.count()).isEqualTo(0L);    	
    }

    @Test
    public void testUserDeletionAlsoRemovesChatroomUsers() {
    	User user1 = givenRandomUserIsRegistered();
    	User user2 = givenRandomUserIsRegistered();
    	User user3 = givenRandomUserIsRegistered();
    	TestUtil.createChatroom(this, user1, user2);
    	TestUtil.createChatroom(this, user1, user3);
    	TestUtil.createChatroom(this, user2, user3);
    	
        assertThat(userRepo.count()).isEqualTo(3L);
        assertThat(chatroomUserRepo.count()).isEqualTo(6L);
        assertThat(chatroomRepo.count()).isEqualTo(3L);
        
    	userRepo.delete(user1.getId());
    	
        assertThat(userRepo.count()).isEqualTo(2L);
        assertThat(chatroomUserRepo.count()).isEqualTo(4L);
        assertThat(chatroomRepo.count()).isEqualTo(3L);
	}
    
    @Test
    public void testUserDeletionAlsoRemovesPasswordResetToken() {
    	User user = givenRandomUserIsRegistered();
    	passwordResetTokenRepo.save(new PasswordResetToken("test", user));

        assertThat(userRepo.count()).isEqualTo(1);
        assertThat(passwordResetTokenRepo.count()).isEqualTo(1);

    	userRepo.delete(user.getId());

        assertThat(userRepo.count()).isEqualTo(0);
        assertThat(passwordResetTokenRepo.count()).isEqualTo(0);
    }
    
    @Test
    public void testChatroomDeletionAlsoRemovesChatroomUsers() {
    	User user1 = givenRandomUserIsRegistered();
    	User user2 = givenRandomUserIsRegistered();
    	User user3 = givenRandomUserIsRegistered();
    	Chatroom room = TestUtil.createChatroom(this, user1, user2);
    	TestUtil.createChatroom(this, user1, user3);
    	TestUtil.createChatroom(this, user2, user3);
    	
        assertThat(userRepo.count()).isEqualTo(3L);
        assertThat(chatroomUserRepo.count()).isEqualTo(6L);
        assertThat(chatroomRepo.count()).isEqualTo(3L);
        
    	chatroomRepo.delete(room.getId());
    	
        assertThat(userRepo.count()).isEqualTo(3L);
        assertThat(chatroomUserRepo.count()).isEqualTo(4L);
        assertThat(chatroomRepo.count()).isEqualTo(2L);
	}
    
    private CommentVote givenUserLikedOrDislikedComment(User commenter, Comment child) {
        return commentVoteRepo.save(TestUtil.createRandomCommentVote(commenter, child));
	}

	private User givenRandomUserIsRegistered() {
        return TestUtil.createRandomUser(this);
    }

    private Topic givenUserCreatedTopic(User user) {
        return TestUtil.createRandomTopic(this, user);
    }

    private Comment givenUserCommentedTopic(User user, Topic topic) {
        return TestUtil.createRandomComment(this, topic, user, null);
    }

    private Comment givenUserRepliedToComment(User user, Topic topic, Comment comment) {
        return TestUtil.createRandomComment(this, topic, user, comment);
    }

    private CommentVote givenUserVotedComment(User user, Comment comment) {
    	return TestUtil.createRandomCommentVote(this, user, comment);
    }
    
    private TopicVote givenUserVotedTopic(User user, Topic topic) {
    	return TestUtil.createRandomTopicVote(this, user, topic);
    }
    
}
