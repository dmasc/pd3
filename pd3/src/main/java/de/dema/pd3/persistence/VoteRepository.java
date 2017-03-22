package de.dema.pd3.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import de.dema.pd3.persistence.Vote.VotePk;

public interface VoteRepository extends CrudRepository<Vote, VotePk> {
	
	Page<Vote> findByVotePkUser(User user, Pageable pageable);
	
	int countByVotePkTopic(Topic topic);

}
