package com.ExpenseTracker.Authentication.Serializer;

import com.ExpenseTracker.Authentication.EventProducers.UserInfoEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
        if(userInfoEvent == null){
            return null;
        }
        byte[] serializedData = null;
        ObjectMapper mapper = new ObjectMapper();
        try{
            serializedData = mapper.writeValueAsString(userInfoEvent).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return serializedData;
    }

    @Override
    public void close() {
    }
}
