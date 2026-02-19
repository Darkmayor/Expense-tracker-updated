package com.ExpenseTracker.Authentication.Repository;

import com.ExpenseTracker.Authentication.Entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Long> {

    Optional<RefreshToken> findByToken(String token);

}
