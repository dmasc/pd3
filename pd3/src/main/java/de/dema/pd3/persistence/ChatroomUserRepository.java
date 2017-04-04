package de.dema.pd3.persistence;

import org.springframework.data.repository.CrudRepository;

public interface ChatroomUserRepository extends CrudRepository<ChatroomUser, ChatroomUserId> {

	int countByIdChatroomId(Long roomId);

}
