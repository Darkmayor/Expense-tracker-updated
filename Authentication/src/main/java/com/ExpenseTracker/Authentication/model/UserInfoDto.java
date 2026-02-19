package com.ExpenseTracker.Authentication.model;

import com.ExpenseTracker.Authentication.Entities.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class UserInfoDto extends UserInfo {

    @jakarta.validation.constraints.NotBlank(message = "First name is required")
    private String firstName; // first_name
    @jakarta.validation.constraints.NotBlank(message = "Last name is required")
    private String lastName; //last_name
    @jakarta.validation.constraints.NotNull(message = "Phone number is required")
    private Long phoneNumber;
    @jakarta.validation.constraints.NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email; // email


}
