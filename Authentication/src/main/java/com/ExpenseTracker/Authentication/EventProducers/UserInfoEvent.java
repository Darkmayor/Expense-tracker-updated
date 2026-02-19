package com.ExpenseTracker.Authentication.EventProducers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true) // if there are any null values while setting then
public class UserInfoEvent {

    private String firstName;
    private String lastName;
    private String userId;
    private String email;
    private Long phoneNumber;

}
