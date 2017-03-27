package de.dema.pd3.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Ronny on 25.03.2017.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class EventRecipient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name = UUID.randomUUID().toString();

    @Column(nullable = false)
    private Character type;

    private Boolean notificationsActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public Boolean getNotificationsActive() {
        return notificationsActive;
    }

    public void setNotificationsActive(Boolean notificationsActive) {
        this.notificationsActive = notificationsActive;
    }
}
