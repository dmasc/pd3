package de.dema.pd3.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import de.dema.pd3.Clock;
import de.dema.pd3.DBTestBase;
import de.dema.pd3.TestUtil;

public class MessageRepositoryTest extends DBTestBase {

	@Test
	public void testLoadMoreMessagesLoadsCorrectMessages() {
		User user1 = TestUtil.createRandomUser(this);
		User user2 = TestUtil.createRandomUser(this);
		Chatroom chatroom = TestUtil.createChatroom(this, user1, user2);
		List<Message> messages = TestUtil.createRandomMessages(this, chatroom, user1, r.nextInt(60) + 40);
		messages.addAll(TestUtil.createRandomMessages(this, chatroom, user2, r.nextInt(60) + 40));
		Collections.sort(messages, (msg1, msg2) -> msg2.getSendTimestamp().compareTo(msg1.getSendTimestamp()));
		
		int size = r.nextInt(10) + 1;
		Page<Message> page = messageRepo.findByRoomIdOrderBySendTimestampDesc(chatroom.getId(), new PageRequest(0, size));
		Message msg = null;
		do {
			assertThat(page.getNumberOfElements()).isEqualTo(Math.min(messages.size(), size));
			assertThat(page.getTotalPages()).isEqualTo(messages.size() / size + (messages.size() % size == 0 ? 0 : 1));
			for (int i = 0; i < size; i++) {
				msg = messages.remove(0);
				assertThat(page.getContent().get(i).getId()).isEqualTo(msg.getId());
			}
			assertThat(page.isLast()).isEqualTo(messages.size() == 0);
			if (!page.isLast()) {
				page = messageRepo.findByRoomIdAndOlderThanParticularMessage(chatroom.getId(), msg.getSendTimestamp(), new PageRequest(0, size));
			}
			
			msg = TestUtil.createRandomMessage(chatroom, user2);
			msg.setSendTimestamp(Clock.now());
			messageRepo.save(msg);
		} while (!page.isLast());
	}
	
}
