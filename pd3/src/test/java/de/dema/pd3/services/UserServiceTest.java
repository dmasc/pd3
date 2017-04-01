package de.dema.pd3.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.dema.pd3.TestUtil;
import de.dema.pd3.persistence.Chatroom;
import de.dema.pd3.persistence.ChatroomRepository;
import de.dema.pd3.persistence.ChatroomUser;
import de.dema.pd3.persistence.ChatroomUserRepository;
import de.dema.pd3.persistence.MessageRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepo;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private ChatroomRepository chatroomRepo;
	
	@Mock
	private ChatroomUserRepository chatroomUserRepo;
	
	@Mock
	private MessageRepository messageRepo;

	@Mock
	private User user;

	@InjectMocks
	private UserService service = new UserService();

	@Before
	public void init() {
		when(userRepo.findOne(anyLong())).thenReturn(user);
	}
	
	@Test
	public void testAreNewMessagesAvailable() {
		Set<ChatroomUser> set = new HashSet<>();
		when(user.getChatroomUsers()).thenReturn(set);

		LocalDateTime ts = TestUtil.createRandomDateTime(-2, -4);
		set.add(createChatroomUser(false, TestUtil.createRandomDateTime(-5, -7), ts));
		assertThat(service.areNewMessagesAvailable(1L)).isFalse();

		set.add(createChatroomUser(true, ts, ts));
		assertThat(service.areNewMessagesAvailable(1L)).isFalse();

		ChatroomUser cu = createChatroomUser(true, null, ts);
		set.add(cu);
		assertThat(service.areNewMessagesAvailable(1L)).isTrue();
		
		set.remove(cu);
		cu = createChatroomUser(true, TestUtil.createRandomDateTime(-5, -7), ts);
		set.add(cu);
		assertThat(service.areNewMessagesAvailable(1L)).isTrue();		
	}
	
	private ChatroomUser createChatroomUser(boolean notificationsActive, LocalDateTime lastMessageReadTimestamp, LocalDateTime lastMessageSentTimestamp) {
		ChatroomUser user = mock(ChatroomUser.class);
		Chatroom room = createChatroom(lastMessageSentTimestamp);
		when(user.getChatroom()).thenReturn(room);
		when(user.getLastMessageRead()).thenReturn(lastMessageReadTimestamp);
		when(user.isNotificationsActive()).thenReturn(notificationsActive);
		return user;
	}

	private Chatroom createChatroom(LocalDateTime lastMessageSentTimestamp) {
		Chatroom room = mock(Chatroom.class);
		when(room.getLastMessageSent()).thenReturn(lastMessageSentTimestamp);
		return room;
	}

}
