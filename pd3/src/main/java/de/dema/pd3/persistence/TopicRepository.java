package de.dema.pd3.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends CrudRepository<Topic, Long> {
	
	List<Topic> findByAuthorOrderByCreationDateDesc(User author, Pageable pageable);
	
	List<Topic> findByAuthorOrderByDeadlineDesc(User author, Pageable pageable);
	
	Page<Topic> findAll(Pageable pageable);
	
	@Query("select t from Topic t where deadline > CURRENT_TIMESTAMP")
	Page<Topic> findAllWhereDeadlineGreaterNow(Pageable pageable);
	
	int countByAuthor(User author);

}
