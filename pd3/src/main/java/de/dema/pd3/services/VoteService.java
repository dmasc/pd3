package de.dema.pd3.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.dema.pd3.VoteOption;
import de.dema.pd3.model.VoteModel;
import de.dema.pd3.persistence.Comment;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVote;
import de.dema.pd3.persistence.CommentVoteRepository;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.TopicVote;
import de.dema.pd3.persistence.TopicVoteRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@Service
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteService.class);

    @Autowired
    private TopicVoteRepository topicVoteRepo;

    @Autowired
    private CommentVoteRepository commentVoteRepo;

    @Autowired
    private TopicRepository topicRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private UserRepository userRepo;

    public void storeTopicVote(Long userId, long topicId, VoteOption selectedOption) {
    	log.debug("storing topic vote [userId:{}] [topicId:{}] [selectedOption:{}]", userId, topicId, selectedOption);
        User user = userRepo.findOne(userId);
        Topic topic = topicRepo.findOne(topicId);

        TopicVote vote = topicVoteRepo.findByUserIdAndTopicId(user.getId(), topic.getId());
        if (vote == null) {
            vote = new TopicVote();
            vote.setUser(user);
            vote.setTopic(topic);
        }
        vote.setSelectedOption(selectedOption);
        vote.setVoteTimestamp(LocalDateTime.now());
        vote = topicVoteRepo.save(vote);
        log.info("topic vote stored [vote:{}]", vote);
    }

    public Long storeCommentVote(Long userId, long commentId, VoteOption selectedOption) {
    	log.debug("storing Comment vote [userId:{}] [topicId:{}] [selectedOption:{}]", userId, commentId, selectedOption);
        User user = userRepo.findOne(userId);
        Comment comment = commentRepo.findOne(commentId);

        CommentVote vote = commentVoteRepo.findByUserIdAndCommentId(userId, comment.getId());
        if (vote == null) {
            vote = new CommentVote();
            vote.setUser(user);
            vote.setComment(comment);
        }
        vote.setSelectedOption(selectedOption);
        vote.setVoteTimestamp(LocalDateTime.now());
        vote = commentVoteRepo.save(vote);
        log.info("comment vote stored [vote:{}]", vote);
        return vote.getId();
    }
    
	public Page<VoteModel> findByUserId(Long userId, Pageable pageable) {
		Page<TopicVote> page = topicVoteRepo.findByUserId(userId, pageable);
		return page.map(VoteModel::map);
	}
	
	public VoteModel findByUserIdAndTopicId(Long userId, Long topicId) {
		return VoteModel.map(topicVoteRepo.findByUserIdAndTopicId(userId, topicId));
	}

}
