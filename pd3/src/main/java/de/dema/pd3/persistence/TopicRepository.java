package de.dema.pd3.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TopicRepository extends CrudRepository<Topic, Long> {
	
	List<Topic> findByAuthor(User author, Pageable pageable);
	
	@Query("SELECT t FROM Topic t WHERE deadline > CURRENT_TIMESTAMP AND NOT EXISTS (SELECT 1 FROM Vote v WHERE v.votePk.topic = t AND v.votePk.user = :user)")
	Page<Topic> findAllWhereDeadlineGreaterNowAndUserHasntVotedYet(@Param("user") User user, Pageable pageable);
	
	int countByAuthor(User author);

}
