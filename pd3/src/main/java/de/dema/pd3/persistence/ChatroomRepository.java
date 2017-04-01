package de.dema.pd3.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ChatroomRepository extends CrudRepository<Chatroom, Long> {

	@Query("select count(msg.id) from Chatroom room join room.messages msg join room.users user "
			+ "where room.id = :roomId and user.id.user.id = :userId and msg.sender.id <> :userId and (user.lastMessageRead is null or user.lastMessageRead < msg.sendTimestamp)")
	int countNewMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);
	
	@Query("select room from Chatroom room where size(room.users) = 2 "
			+ "and exists (select 1 from ChatroomUser u where u.id.chatroom = room and u.id.user.id = :first) "
			+ "and exists (select 1 from ChatroomUser u where u.id.chatroom = room and u.id.user.id = :second)")
	Chatroom findBilateralRoom(@Param("first") Long firstId, @Param("second") Long secondId);
	
}
