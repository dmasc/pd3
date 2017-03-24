package de.dema.pd3.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.dema.pd3.model.CommentModel;
import de.dema.pd3.persistence.Comment;
import de.dema.pd3.persistence.CommentRepository;
import de.dema.pd3.persistence.CommentVote;
import de.dema.pd3.persistence.CommentVoteRepository;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.UserRepository;

@Service
public class CommentService {

	private static final Logger log = LoggerFactory.getLogger(CommentService.class);
	
	@Autowired
	private TopicRepository topicRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CommentVoteRepository commentVoteRepo;

    @Autowired
    private CommentRepository commentRepo;
    
    public Page<CommentModel> loadByTopic(Long topicId, Long userId, Pageable pageable) {
    	Page<Comment> page = commentRepo.findRootCommentsByTopic(topicId, pageable);
    	return page.map(comment -> CommentModel.map(comment, t -> {
		    	CommentVote vote = commentVoteRepo.findByUserIdAndCommentId(userId, t.getId());
		    	if (vote != null) {
		    		return vote.getSelectedOption();
		    	}
				return null;
		}));
    }

	public Long save(Long userId, Long topicId, String text) {
		log.info("saving comment [userId:{}] [topicId:{}] [text:{}]");
		Comment comment = new Comment();
		comment.setAuthor(userRepo.findOne(userId));
		comment.setTopic(topicRepo.findOne(topicId));
		comment.setText(text);
		comment.setCreationDate(LocalDateTime.now());
		return commentRepo.save(comment).getId();
	}

	public Long saveReply(Long userId, Long commentId, String text) {
		log.info("saving reply to comment [userId:{}] [commentId:{}] [text:{}]");
		Comment parent = commentRepo.findOne(commentId);
		Comment comment = new Comment();
		comment.setParent(parent);
		comment.setAuthor(userRepo.findOne(userId));
		comment.setTopic(parent.getTopic());
		comment.setText(text);
		comment.setCreationDate(LocalDateTime.now());
		return commentRepo.save(comment).getId();
	}
    
}
