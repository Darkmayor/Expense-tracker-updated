package com.ExpenseTracker.Authentication.Repository;

import com.ExpenseTracker.Authentication.Entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserInfo , String> {

    public UserInfo findByUsername(String username);
}
