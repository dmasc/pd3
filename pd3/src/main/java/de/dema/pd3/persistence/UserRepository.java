package de.dema.pd3.persistence;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByEmail(String email);

}
