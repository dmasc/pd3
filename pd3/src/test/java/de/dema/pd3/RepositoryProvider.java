package de.dema.pd3;

import de.dema.pd3.persistence.ChatroomRepository;
import de.dema.pd3.persistence.ChatroomUserRepository;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVoteRepository;
import de.dema.pd3.persistence.MessageRepository;
import de.dema.pd3.persistence.PasswordResetTokenRepository;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.TopicVoteRepository;
import de.dema.pd3.persistence.UserRepository;

public interface RepositoryProvider {

	ChatroomRepository getChatroomRepository();
	
	ChatroomUserRepository getChatroomUserRepository();
	
	CommentRepository getCommentRepository();
	
	CommentVoteRepository getCommentVoteRepository();
	
	MessageRepository getMessageRepository();
	
	TopicRepository getTopicRepository();
	
	TopicVoteRepository getTopicVoteRepository();
	
	UserRepository getUserRepository();

	PasswordResetTokenRepository getPasswordResetTokenRepository();
		
}
