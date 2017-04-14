package de.dema.pd3.persistence;

import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long> {

	int deleteByOwnerId(Long ownerId);

}
