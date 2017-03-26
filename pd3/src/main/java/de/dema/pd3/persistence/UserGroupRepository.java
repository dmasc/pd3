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

}
