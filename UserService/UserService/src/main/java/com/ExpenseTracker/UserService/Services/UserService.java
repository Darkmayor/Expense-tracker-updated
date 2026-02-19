package com.ExpenseTracker.UserService.Services;

import com.ExpenseTracker.UserService.Entities.UserInfo;
import com.ExpenseTracker.UserService.Entities.UserInfoDTO;
import com.ExpenseTracker.UserService.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Save or update a user.
     * Evicts the cache for this userId so the next GET fetches fresh data from DB.
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userInfoDTO.userId")
    public UserInfoDTO SaveUser(UserInfoDTO userInfoDTO) {
        UserInfo userInfo = userRepository.findByUserId(userInfoDTO.getUserId())
                .map(existing -> userRepository.save(userInfoDTO.transformToUserInfo()))
                .orElseGet(() -> userRepository.save(userInfoDTO.transformToUserInfo()));

        return toDTO(userInfo);
    }

    /**
     * Get user by ID.
     * Result is cached in Redis under key "users::<userId>".
     * On cache hit: returns from Redis (no DB call).
     * On cache miss: fetches from DB, stores in Redis, returns result.
     */
    @Cacheable(value = "users", key = "#userId")
    public UserInfoDTO getUserById(String userId) throws Exception {
        Optional<UserInfo> optionalUserInfo = userRepository.findByUserId(userId);
        if (optionalUserInfo.isEmpty()) {
            throw new Exception("No such user exists: " + userId);
        }
        return toDTO(optionalUserInfo.get());
    }

    /**
     * Legacy method — kept for backward compatibility with Kafka consumer.
     */
    public UserInfoDTO getUser(UserInfoDTO userInfoDTO) throws Exception {
        return getUserById(userInfoDTO.getUserId());
    }

    private UserInfoDTO toDTO(UserInfo userInfo) {
        return UserInfoDTO.builder()
                .userId(userInfo.getUserId())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .email(userInfo.getEmail())
                .phoneNumber(userInfo.getPhoneNumber())
                .profilePic(userInfo.getProfilePic())
                .build();
    }
}
