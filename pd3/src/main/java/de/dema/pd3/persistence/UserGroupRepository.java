package de.dema.pd3.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by Ronny on 25.03.2017.
 */
public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

    List<UserGroup> findByMembersIn(User user);

    UserGroup findOneByMembersInAndMembersIn(User member1, User member2);

    @Query(value = "SELECT u FROM UserGroup u WHERE u.members.size=2 AND :member1 MEMBER OF u.members AND :member2 MEMBER OF u.members")
    UserGroup findUserGroupForDialogBetweenMembers(@Param("member1") User member1, @Param("member2") User member2);

}
