package com.ExpenseTracker.Authentication.Services;

import com.ExpenseTracker.Authentication.Entities.UserInfo;
import com.ExpenseTracker.Authentication.Entities.UserRoles;
import com.ExpenseTracker.Authentication.EventProducers.UserInfoEvent;
import com.ExpenseTracker.Authentication.EventProducers.UserInfoProducer;
import com.ExpenseTracker.Authentication.Repository.RoleRepository;
import com.ExpenseTracker.Authentication.Repository.UserRepository;
import com.ExpenseTracker.Authentication.model.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Data
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;

    private static final Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

//    public UserDetailServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {

        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepository.findByUsername(username);
        if(user == null){
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetailService(user);
    }

    @Autowired
    private final RoleRepository roleRepository;

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public UserInfo signupUser(UserInfoDto userInfoDto){
        //        ValidationUtil.validateUserAttributes(userInfoDto);
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))){
            return null;
        }
        String userId = UUID.randomUUID().toString();
        
        Set<UserRoles> roles = new HashSet<>();
        // Assign default role
        UserRoles userRole = roleRepository.findByName("ROLE_USER").orElse(null);
        if (userRole == null) {
            userRole = new UserRoles();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
        roles.add(userRole);

        UserInfo userInfo = new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), roles);
        userRepository.save(userInfo);
        // pushEventToQueue
        userInfoProducer.sendEventToKafka(userInfoEventMapper(userId , userInfoDto));
        return userInfo;
    }
    public String getUserByUsername(String userName){
        return Optional.of(userRepository.findByUsername(userName)).map(UserInfo::getUserId).orElse(null);
    }

    private UserInfoEvent userInfoEventMapper(String userId, UserInfoDto userInfoDto){
        return UserInfoEvent.builder()
                .firstName(userInfoDto.getFirstName())
                .lastName(userInfoDto.getLastName())
                .email(userInfoDto.getEmail())
                .userId(userId)
                .phoneNumber(userInfoDto.getPhoneNumber())
                .build();
    }
}
