package de.dema.pd3.persistence;

import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {

	Comment findByText(String text);
}
