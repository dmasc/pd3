package de.dema.pd3.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface TopicVoteRepository extends CrudRepository<TopicVote, Long> {
	
	Page<TopicVote> findByUser(User user, Pageable pageable);
	
	TopicVote findByUserAndTopic(User user, Topic topic);
	
	int countByTopic(Topic topic);

}
