package de.dema.pd3.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import de.dema.pd3.FullContextTestBase;
import de.dema.pd3.TestUtil;

public class ChatroomTest extends FullContextTestBase {

	@After
	public void init() {
		clearTables();
	}
	
	@Test
	public void testCountNewMessages() {
		User sender = TestUtil.createRandomUser(this);
		User recipient = TestUtil.createRandomUser(this);
		Chatroom chatroom = TestUtil.createChatroom(this, sender, recipient);
		List<Message> senderMessages = TestUtil.createRandomMessages(this, chatroom, sender, 25);
		List<Message> recipientMessages = TestUtil.createRandomMessages(this, chatroom, recipient, 15);
		
		int newMessagesCount = chatroomRepo.countNewMessages(chatroom.getId(), sender.getId());
		assertThat(newMessagesCount).isEqualTo(15);

		newMessagesCount = chatroomRepo.countNewMessages(chatroom.getId(), recipient.getId());
		assertThat(newMessagesCount).isEqualTo(25);

		chatroom.getUsers().forEach(u -> {
			if (u.getId().getUser().getId().equals(sender.getId())) {
				u.setLastMessageRead(recipientMessages.get(recipientMessages.size() - 3).getSendTimestamp());
			} else {
				u.setLastMessageRead(senderMessages.get(senderMessages.size() - 7).getSendTimestamp());
			}
			chatroomUserRepo.save(u);
		});
		
		newMessagesCount = chatroomRepo.countNewMessages(chatroom.getId(), sender.getId());
		assertThat(newMessagesCount).isEqualTo(2);

		newMessagesCount = chatroomRepo.countNewMessages(chatroom.getId(), recipient.getId());
		assertThat(newMessagesCount).isEqualTo(6);
	}
	
	@Test
	public void testFindBilateralRoom() {
		User u1 = TestUtil.createRandomUser(this);
		User u2 = TestUtil.createRandomUser(this);
		User u3 = TestUtil.createRandomUser(this);
		User u4 = TestUtil.createRandomUser(this);
		
		Chatroom chatroom12 = TestUtil.createChatroom(this, u1, u2);
		Chatroom chatroom13 = TestUtil.createChatroom(this, u1, u3);
		Chatroom chatroom23 = TestUtil.createChatroom(this, u2, u3);
		
		Chatroom bilateralRoom = chatroomRepo.findBilateralRoom(u1.getId(), u2.getId());
		assertThat(bilateralRoom).isNotNull();
		assertThat(bilateralRoom.getId().equals(chatroom12.getId()));

		bilateralRoom = chatroomRepo.findBilateralRoom(u3.getId(), u2.getId());
		assertThat(bilateralRoom).isNotNull();
		assertThat(bilateralRoom.getId().equals(chatroom23.getId()));

		bilateralRoom = chatroomRepo.findBilateralRoom(u3.getId(), u1.getId());
		assertThat(bilateralRoom).isNotNull();
		assertThat(bilateralRoom.getId().equals(chatroom13.getId()));

		bilateralRoom = chatroomRepo.findBilateralRoom(u2.getId(), u4.getId());
		assertThat(bilateralRoom).isNull();
	}

	@Test
	public void testFindRoomMessages() {
		int msgCountSender = 25;
		int msgCountRecipient = 16;
		int msgPerPage = 15;
		
		User sender = TestUtil.createRandomUser(this);
		User recipient = TestUtil.createRandomUser(this);
		Chatroom chatroom = TestUtil.createChatroom(this, sender, recipient);
		List<Message> messages = TestUtil.createRandomMessages(this, chatroom, sender, msgCountSender);
		messages.addAll(TestUtil.createRandomMessages(this, chatroom, recipient, msgCountRecipient));
		Collections.sort(messages, (msg1, msg2) -> msg2.getSendTimestamp().compareTo(msg1.getSendTimestamp()));
		
		int expectedTotalPages = (msgCountSender + msgCountRecipient) / msgPerPage + 1;
		for (int p = 0; p < expectedTotalPages; p++) {
			Page<Message> page = messageRepo.findByRoomIdOrderBySendTimestampDesc(chatroom.getId(), new PageRequest(p, msgPerPage));
			assertThat(page.getNumberOfElements()).isEqualTo(Math.min(msgCountSender + msgCountRecipient - p * msgPerPage, msgPerPage));
			assertThat(page.getTotalPages()).isEqualTo(expectedTotalPages);
			assertThat(page.getTotalElements()).isEqualTo(msgCountSender + msgCountRecipient);
			for (int i = 0; i < page.getNumberOfElements(); i++) {
				assertThat(page.getContent().get(i).getId()).isEqualTo(messages.get(p * msgPerPage + i).getId());
			}
		}
	}
	
}
