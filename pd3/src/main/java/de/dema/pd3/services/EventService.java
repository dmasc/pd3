package de.dema.pd3.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dema.pd3.model.EventModel;
import de.dema.pd3.model.events.EventTypes;
import de.dema.pd3.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Ronny on 25.03.2017.
 */
@Service
public class EventService {

    private static final ObjectMapper OM = new ObjectMapper();

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRecipientRepository eventRecipientRepository;

    public List<EventModel> getEventsFor(Pageable page, Long id, int type) {
        List<EventRecipient> recipientList = new ArrayList<>();
        User user = userRepository.findOne(id);
        if (user != null) {
            recipientList.add(user);
        } else {
            UserGroup group = userGroupRepository.findOne(id);
            recipientList.add(group);
        }
        List<EventModel> result = new ArrayList<>();
        eventRepository.findByTypeAndRecipientsIn(type, recipientList, page).forEach(event -> result.add(EventModel.map(event)));
        return result;
    }

    public List<EventModel> getEventsForUser(Pageable page, Long userId, int type) {
        List<EventRecipient> recipientList = new ArrayList<>();
        User user = userRepository.findOne(userId);
        recipientList.add(user);

        List<UserGroup> groups = userGroupRepository.findByMembersIn(user);
        for (UserGroup group : groups) {
            recipientList.add(group);
        }
        List<EventModel> result = new ArrayList<>();
        eventRepository.findByTypeAndRecipientsIn(type, recipientList, page).forEach(event -> result.add(EventModel.map(event)));
        return result;
    }

    public void sendEvent(EventModel eventModel) throws Exception {
        sendEvent(eventModel.getType(), eventModel.getSender(), Optional.ofNullable(eventModel.getPayload()), eventModel.getRecipients());
    }

    public void sendEvent(EventTypes type,
                          String sender,
                          Optional<? extends Serializable> payload,
                          Set<Long> recipients) throws Exception {
        Event event = new Event();
        event.setType(type.getId());
        event.setSender(sender);
        if (payload.isPresent()) {
            event.setPayload(transformPayload(payload.get()));
        }

        Set<EventRecipient> listOfRecipients = new HashSet<>();
        if (recipients != null && recipients.size() > 0) {
            Iterable<EventRecipient> eventRecipients = eventRecipientRepository.findAll(recipients);
            for (EventRecipient eventRecipient : eventRecipients) {
                listOfRecipients.add(eventRecipient);
            }
        }
        if (!listOfRecipients.isEmpty()) {
            event.setRecipients(listOfRecipients);
            eventRepository.save(event);
        }
    }

    private String transformPayload(Serializable payload) throws Exception {
        return payload instanceof String ? (String) payload : OM.writeValueAsString(payload);
    }

}
