package de.dema.pd3.persistence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created by Ronny on 25.03.2017.
 */
public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

    @Query(value = "SELECT ug FROM UserGroup as ug INNER JOIN ug.members AS u WHERE u.user = :user")
    List<UserGroup> findByMembersIn(@Param("user") User user);

    @Query(value = "SELECT ug FROM UserGroup ug INNER JOIN ug.members AS u WHERE (u.user = :member1 OR u.user = :member2) AND ug.members.size = 2 GROUP BY ug")
    UserGroup findUserGroupForDialogBetweenMembers(@Param("member1") User member1, @Param("member2") User member2);

    @Transactional
    @Modifying
    @Query(value = "UPDATE UserGroupMembers AS ugm SET ugm.notificationsActive = :notificationsActive WHERE ugm.group.id = :groupId AND ugm.user.id = :userId")
    void updateMembersNotificationStatus(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("notificationsActive") Boolean notificationsActive);

    @Transactional
    @Modifying
    @Query(value = "UPDATE UserGroupMembers AS ugm SET ugm.lastCheckForMessages = :lastCheckDate WHERE ugm.group.id = :groupId AND ugm.user.id = :userId")
    void updateMembersLastCheckDatte(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("lastCheckDate") LocalDateTime lastCheckDate);

}
