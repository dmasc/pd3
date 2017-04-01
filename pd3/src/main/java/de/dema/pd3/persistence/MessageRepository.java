package de.dema.pd3.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {

	Page<Message> findByRoomIdOrderBySendTimestampDesc(Long roomId, Pageable pageable);
	
}
