package de.dema.pd3.persistence;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by Ronny on 25.03.2017.
 */
@Entity
@Cacheable
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer type;

    private LocalDateTime sendTime;

    private String sender;

    @OneToMany()
    private Set<EventRecipient> recipients;

    private String payload;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public Set<EventRecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<EventRecipient> recipients) {
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

    @Override
    public String toString() {
        return id + " " + sender +" " + payload;
    }
}
