package de.dema.pd3;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

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
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.TopicVoteRepository;
import de.dema.pd3.persistence.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.properties")
public class DBTestBase implements RepositoryProvider {

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
    protected DataSource dataSource;
    
    protected JdbcTemplate jdbc;

	@PostConstruct
	public void setup() {
		jdbc = new JdbcTemplate(dataSource);
	}
	
	@PreDestroy
	public void clearTables() {
		chatroomRepo.deleteAll();
		chatroomUserRepo.deleteAll();
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

}
