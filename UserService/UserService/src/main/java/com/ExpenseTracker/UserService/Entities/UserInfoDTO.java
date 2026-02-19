package com.ExpenseTracker.UserService.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserInfoDTO implements java.io.Serializable {

    private Long id;
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private Long phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("profile_pic")
    private String profilePic;

    public UserInfo transformToUserInfo(){
        return UserInfo.builder()
                .userId(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .profilePic(profilePic)
                .phoneNumber(phoneNumber)
                .build();
    }
}
