package de.dema.pd3.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByEmail(String email);

	@Query("select u from User u where lower(concat(u.forename, ' ', u.surname)) like lower(:query) order by u.forename, u.surname)")
	List<User> findByQuery(@Param("query") String query);

}
