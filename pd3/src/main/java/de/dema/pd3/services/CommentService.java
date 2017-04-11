package de.dema.pd3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.dema.pd3.Clock;
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
		log.info("saving comment [userId:{}] [topicId:{}] [text:{}]", userId, topicId, text);
		Comment.Builder comment = new Comment.Builder().author(userRepo.findOne(userId)).creationDate(Clock.now())
				.topic(topicRepo.findOne(topicId)).text(text);
		return commentRepo.save(comment.build()).getId();
	}

	public Long saveReply(Long userId, Long commentId, String text) {
		log.info("saving reply to comment [userId:{}] [commentId:{}] [text:{}]", userId, commentId, text);
		Comment parent = commentRepo.findOne(commentId);
		if (parent != null) {
			Comment.Builder comment = new Comment.Builder().author(userRepo.findOne(userId)).creationDate(Clock.now())
					.parent(parent).topic(parent.getTopic()).text(text);
			return commentRepo.save(comment.build()).getId();
		}
		log.warn("unable to find parent comment [userId:{}] [commentId:{}]", userId, commentId);
		return -1L;
	}

	public void deleteComment(Long commentId) {
		commentRepo.delete(commentId);		
	}
	
	public Long findUserIdOfComment(Long commentId) {
		return commentRepo.findOne(commentId).getAuthor().getId();
	}
    
}
