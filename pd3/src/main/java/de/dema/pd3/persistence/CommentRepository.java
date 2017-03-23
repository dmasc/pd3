package de.dema.pd3.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends CrudRepository<Comment, Long> {

	Comment findByText(String text);
	
	@Query("SELECT c FROM Comment c WHERE c.topic.id = :topicId AND c.parent IS NULL")
	Page<Comment> findRootCommentsByTopic(@Param("topicId") Long topicId, Pageable pageable);
	
}
