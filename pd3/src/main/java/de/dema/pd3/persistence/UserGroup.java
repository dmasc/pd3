package de.dema.pd3.persistence;

import javax.annotation.processing.SupportedOptions;
import javax.persistence.*;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Ronny on 25.03.2017.
 */
@Entity
@Cacheable
public class UserGroup extends EventRecipient {

    @Column(nullable = false, unique = false)
    private LocalDateTime created = LocalDateTime.now();

    @ManyToMany
    private List<User> members;

    public UserGroup() {
        setType('g');
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group :" + getName();
    }
}
