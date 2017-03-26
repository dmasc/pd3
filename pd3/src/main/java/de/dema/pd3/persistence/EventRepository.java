package de.dema.pd3.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by Ronny on 25.03.2017.
 */
public interface EventRepository extends CrudRepository<Event, Long> {

    Page<Event> findByTypeAndRecipientsIn(Integer type, List<EventRecipient> recipients, Pageable pageable);

}
