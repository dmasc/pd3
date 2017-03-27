package de.dema.pd3.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dema.pd3.model.EventModel;
import de.dema.pd3.model.events.EventTypes;
import de.dema.pd3.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
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
        EventRecipient recipient =  eventRecipientRepository.findOne(id);
        List<EventModel> result = new ArrayList<>();
        eventRepository.findByTypeAndRecipientsInOrderBySendTimeDesc(type, recipient, page).forEach(
                event -> result.add(EventModel.map(event)));
        return result;
    }

    public void sendEvent(EventModel eventModel) throws Exception {
        EventRecipient sender = eventRecipientRepository.findOne(eventModel.getSenderId());
        sendEvent(eventModel.getType(), sender, Optional.ofNullable(eventModel.getPayload()), eventModel.getRecipients());
    }

    public void sendEvent(EventTypes type,
                          EventRecipient sender,
                          Optional<String> payload,
                          Set<Long> recipients) throws Exception {
        Event event = new Event();
        event.setType(type.getId());
        event.setSender(sender);
        if (payload.isPresent()) {
            event.setPayload(payload.get());
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
            event.setSendTime(LocalDateTime.now());
            eventRepository.save(event);
        }
    }

    /**
     * Kodiert alle nicht Strings nach json
     * @param payload Ein serialisierbares Object
     * @return payload als String wie er war oder json Representation des payload
     * @throws Exception
     */
    public static String encodePayload(Serializable payload) throws Exception {
        return payload instanceof String ? (String) payload : OM.writeValueAsString(payload);
    }

    /**
     * Kodiert json zu String oder gibt den String an sich zurück
     * @param payload String oder json Objekt Representation
     * @return Ein serialisierbares Object
     * @throws Exception
     */
    public static Serializable decodePayload(String payload) throws Exception {
        if (payload != null && payload.startsWith("Object::")) {
            return OM.readValue(payload.replaceFirst("Object::", ""), Serializable.class);
        }
        return payload;
    }

}
