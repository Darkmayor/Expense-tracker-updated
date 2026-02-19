package com.ExpenseTracker.UserService.Deserializer;

import com.ExpenseTracker.UserService.Entities.UserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class UserInfoDeserializer implements Deserializer<UserInfoDTO> {


    Logger logger = LoggerFactory.getLogger(UserInfoDeserializer.class);
    @Override
    public UserInfoDTO deserialize(String s, byte[] bytes) {
        if(bytes == null){
            return null;
        }
        UserInfoDTO user = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            user = objectMapper.readValue(bytes , UserInfoDTO.class);
        }catch (Exception e){
            logger.error("Could Not Deserialize and map the object");
        }
        return user;
    }

    @Override public void close() {
    }
    @Override public void configure(Map<String, ?> arg0, boolean arg1) {
    }
}
