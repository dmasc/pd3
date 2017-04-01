package de.dema.pd3.persistence;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Ronny on 29.03.2017.
 */
@Entity
@Table
public class UserGroupMembers implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    private EventRecipient group;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private EventRecipient user;

    @Column(columnDefinition="tinyint(1) default 0")
    private Boolean notificationsActive;

    private LocalDateTime lastCheckForMessages;

    public Boolean getNotificationsActive() {
        return notificationsActive;
    }

    public void setNotificationsActive(Boolean notificationsActive) {
        this.notificationsActive = notificationsActive;
    }

    public EventRecipient getGroup() {
        return group;
    }

    public void setGroup(EventRecipient group) {
        this.group = group;
    }

    public EventRecipient getUser() {
        return user;
    }

    public void setUser(EventRecipient user) {
        this.user = user;
    }

    public LocalDateTime getLastCheckForMessages() {
        return lastCheckForMessages;
    }

    public void setLastCheckForMessages(LocalDateTime lastCheckForMessages) {
        this.lastCheckForMessages = lastCheckForMessages;
    }

}
