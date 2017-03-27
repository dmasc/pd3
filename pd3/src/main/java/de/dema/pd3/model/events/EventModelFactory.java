package de.dema.pd3.model.events;

import de.dema.pd3.model.EventModel;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Ronny on 25.03.2017.
 */
public class EventModelFactory {

    /**
     * Create a new text message
     * @param sender Sender name
     * @param msg Message
     * @param recipients Group and User id's
     * @return Event for sending text message
     */
    public static EventModel createUserMessage(String sender, Long senderId, String msg, Long ... recipients) {
        if (recipients == null) {
            throw new NullPointerException("Cannot create EventModel without recipients");
        }
        EventModel msgEvent = createEvent(EventTypes.USER_MESSAGE);
        msgEvent.setSender(sender);
        msgEvent.setSenderId(senderId);
        msgEvent.setPayload(msg);
        msgEvent.setRecipients(new HashSet<>(Arrays.asList(recipients)));
        return msgEvent;
    }

    private static EventModel createEvent(EventTypes eventType) {
        EventModel event = new EventModel();
        event.setTimestamp(LocalDateTime.now());
        event.setType(eventType);
        return event;
    }

}
