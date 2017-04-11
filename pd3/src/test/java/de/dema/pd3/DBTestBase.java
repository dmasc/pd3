package de.dema.pd3;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import de.dema.pd3.persistence.ChatroomRepository;
import de.dema.pd3.persistence.ChatroomUserRepository;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVoteRepository;
import de.dema.pd3.persistence.MessageRepository;
import de.dema.pd3.persistence.PasswordResetTokenRepository;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.TopicVoteRepository;
import de.dema.pd3.persistence.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.properties")
public class DBTestBase implements RepositoryProvider {
	
	protected static final Random r = new Random(); 

    @Autowired
    protected TopicRepository topicRepo;

    @Autowired
    protected TopicVoteRepository topicVoteRepo;
    
    @Autowired
    protected UserRepository userRepo;

    @Autowired
    protected TopicVoteRepository voteRepo;

    @Autowired
    protected CommentRepository commentRepo;

    @Autowired
    protected CommentVoteRepository commentVoteRepo;
    
    @Autowired
    protected ChatroomRepository chatroomRepo;
    
    @Autowired
    protected ChatroomUserRepository chatroomUserRepo;
    
    @Autowired
    protected MessageRepository messageRepo;
    
    @Autowired
	protected PasswordResetTokenRepository passwordResetTokenRepo;
    
    @Autowired
    protected DataSource dataSource;
    
    protected JdbcTemplate jdbc;

	@PostConstruct
	public void setup() {
		jdbc = new JdbcTemplate(dataSource);
	}
	
	@Before
	public void clearTables() {
		chatroomRepo.deleteAll();
		topicRepo.deleteAll();
		userRepo.deleteAll();
	}

	@Override
	public ChatroomRepository getChatroomRepository() {
		return chatroomRepo;
	}

	@Override
	public ChatroomUserRepository getChatroomUserRepository() {
		return chatroomUserRepo;
	}

	@Override
	public CommentRepository getCommentRepository() {
		return commentRepo;
	}

	@Override
	public CommentVoteRepository getCommentVoteRepository() {
		return commentVoteRepo;
	}

	@Override
	public MessageRepository getMessageRepository() {
		return messageRepo;
	}

	@Override
	public TopicRepository getTopicRepository() {
		return topicRepo;
	}

	@Override
	public TopicVoteRepository getTopicVoteRepository() {
		return topicVoteRepo;
	}

	@Override
	public UserRepository getUserRepository() {
		return userRepo;
	}

	@Override
	public PasswordResetTokenRepository getPasswordResetTokenRepository() {
		return passwordResetTokenRepo;
	}

}
