package de.dema.pd3.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Ronny on 25.03.2017.
 */
public interface EventRepository extends CrudRepository<Event, Long> {

    @Query(value = "SELECT e FROM Event e INNER JOIN e.recipients AS r WHERE r = :recipient AND e.type = :type ORDER BY e.sendTime DESC")
    Page<Event> findByTypeAndRecipientsInOrderBySendTimeDesc(@Param("type") Integer type,
                                                             @Param("recipient") EventRecipient recipient,
                                                             Pageable pageable);

    @Query(value = "SELECT COUNT(e.id) FROM Event e INNER JOIN e.recipients AS r WHERE r = :recipient AND e.type = :type AND e.sendTime > :lastCheck")
    Integer countEventsByTypeOfRecipient(@Param("type") Integer type,
                                         @Param("lastCheck") LocalDateTime lastCheck,
                                         @Param("recipient") EventRecipient recipient);

    @Query(value = "SELECT MAX(e.sendTime) FROM Event e INNER JOIN e.recipients AS r WHERE r = :recipient AND e.type = :type")
    LocalDateTime findEarliestSendTimeOfEventsByTypeOfRecipient(@Param("type") Integer type,
                                                                @Param("recipient") EventRecipient recipient);

}
