package com.ExpenseTracker.UserService.Consumer;

import com.ExpenseTracker.UserService.Entities.UserInfoDTO;
import com.ExpenseTracker.UserService.Services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceConsumer {


    private final UserService userService;

    @Autowired
    public AuthServiceConsumer(UserService userService){
        this.userService = userService;
    }

    @KafkaListener(topics = "${spring.kafka.topic-json.name}" , groupId = "${spring.kafka.consumer.group-id}")
    public void listener(UserInfoDTO eventData){
        try{
            System.out.println("Event consumed");
            userService.SaveUser(eventData);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Failed to consume the event");
        }
    }
}
