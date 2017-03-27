package de.dema.pd3.model;

import de.dema.pd3.model.events.EventTypes;
import de.dema.pd3.persistence.Event;
import de.dema.pd3.persistence.EventRecipient;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ronny on 25.03.2017.
 */
public class EventModel {

    private Long id;

    private EventTypes type;

    private LocalDateTime timestamp;

    private String sender;

    private Long senderId;

    private Set<Long> recipients;

    private String payload;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventTypes getType() {
        return type;
    }

    public void setType(EventTypes type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Set<Long> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<Long> recipients) {
        this.recipients = recipients;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public static EventModel map(Event event) {
        EventModel eventModel = new EventModel();
        eventModel.setId(event.getId());
        eventModel.setSender(event.getSender().getName());
        eventModel.setSenderId(event.getSender().getId());
        Set<Long> recipientIds = new HashSet<>();
        for (EventRecipient eventRecipient : event.getRecipients()) {
            recipientIds.add(eventRecipient.getId());
        }
        eventModel.setRecipients(recipientIds);
        eventModel.setPayload(event.getPayload());
        eventModel.setType(EventTypes.fromId(event.getType()));
        eventModel.setTimestamp(event.getSendTime());
        return eventModel;
    }

}
