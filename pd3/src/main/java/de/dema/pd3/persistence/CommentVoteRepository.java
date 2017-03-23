package de.dema.pd3.persistence;

import org.springframework.data.repository.CrudRepository;

public interface CommentVoteRepository extends CrudRepository<CommentVote, Long> {

	CommentVote findByComment(Comment comment);

	CommentVote findByUserIdAndCommentId(Long userId, Long commentId);
	
}
