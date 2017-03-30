package de.dema.pd3.persistence;

import javax.annotation.processing.SupportedOptions;
import javax.persistence.*;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroupMembers> members;

    public UserGroup() {
        setType('g');
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<UserGroupMembers> getMembers() {
        return members;
    }

    public void setMembers(List<UserGroupMembers> members) {
        this.members = members;
    }

    public void addMembers(User ... members) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        for (User member : members) {
            UserGroupMembers ugm = new UserGroupMembers();
            ugm.setLastCheckForMessages(LocalDateTime.of(2017, 1, 1, 0, 0));
            ugm.setGroup(this);
            ugm.setUser(member);
            this.members.add(ugm);
        }
    }

    public UserGroupMembers getMember(Long userId) {
        for (UserGroupMembers member : getMembers()) {
            if (member.getUser().getId().equals(userId)) {
                return member;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Group :" + getName();
    }

}
