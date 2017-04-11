package de.dema.pd3.persistence;

import org.springframework.data.repository.CrudRepository;

import de.dema.pd3.persistence.ChatroomUser.ChatroomUserId;

public interface ChatroomUserRepository extends CrudRepository<ChatroomUser, ChatroomUserId> {

	int countByIdChatroomId(Long roomId);

}
