package de.dema.pd3.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends CrudRepository<Topic, Long> {
	
	List<Topic> findByAuthor(User author, Pageable pageable);
	
	@Query("SELECT t FROM Topic t WHERE deadline > CURRENT_TIMESTAMP")
	Page<Topic> findAllWhereDeadlineGreaterNow(Pageable pageable);
	
	int countByAuthor(User author);

}
