package de.dema.pd3.persistence;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends CrudRepository<Message, Long> {

	Page<Message> findByRoomIdOrderBySendTimestampDesc(Long roomId, Pageable pageable);

	@Query("select msg from Message msg where room.id = :chatroomId and sendTimestamp < :sendTimestampLimit order by sendTimestamp desc")
	Page<Message> findByRoomIdAndOlderThanParticularMessage(@Param("chatroomId") Long chatroomId, @Param("sendTimestampLimit") LocalDateTime sendTimestampLimit, Pageable pageRequest);
	
}
