package de.dema.pd3.persistence;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import de.dema.pd3.persistence.Vote.VotePk;

public interface VoteRepository extends CrudRepository<Vote, VotePk> {
	
	Set<Vote> findByVotePkUser(User user);
	
	int countByVotePkTopic(Topic topic);

}
