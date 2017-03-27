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

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @OneToOne(optional = true)
    private EventRecipient sender;

    @ManyToMany()
    private Set<EventRecipient> recipients;

    @Lob
    @Column(name="CONTENT", length=4096)
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

    public EventRecipient getSender() {
        return sender;
    }

    public void setSender(EventRecipient sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return id + " " + sender +" " + payload;
    }
}
