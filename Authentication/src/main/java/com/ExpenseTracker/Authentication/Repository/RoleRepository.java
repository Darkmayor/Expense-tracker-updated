package com.ExpenseTracker.Authentication.Repository;

import com.ExpenseTracker.Authentication.Entities.UserRoles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<UserRoles, Long> {
    Optional<UserRoles> findByName(String name);
}
