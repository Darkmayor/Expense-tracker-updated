package com.ExpenseTracker.UserService.Controller;

import com.ExpenseTracker.UserService.Entities.UserInfoDTO;
import com.ExpenseTracker.UserService.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /user/v1/profile/{userId}
     * Fetch a user's profile by their userId.
     * Kong injects X-User-ID header, but we also support direct path variable for flexibility.
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserInfoDTO> getUser(@PathVariable String userId) {
        try {
            UserInfoDTO user = userService.getUserById(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * PUT /user/v1/profile/{userId}
     * Update a user's profile (name, phone, profile picture, etc.)
     * userId from path must match the authenticated user (from X-User-ID header injected by Kong).
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserInfoDTO> updateUser(
            @PathVariable String userId,
            @RequestBody UserInfoDTO userInfoDTO,
            @RequestHeader(value = "X-User-ID", required = false) String authenticatedUserId) {
        try {
            // Security check: ensure the authenticated user can only update their own profile
            if (authenticatedUserId != null && !authenticatedUserId.equals(userId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            userInfoDTO.setUserId(userId);
            UserInfoDTO updated = userService.SaveUser(userInfoDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /user/v1/me
     * Convenience endpoint — get the currently authenticated user's profile.
     * Relies on Kong injecting X-User-ID header.
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getMe(
            @RequestHeader(value = "X-User-ID") String userId) {
        try {
            UserInfoDTO user = userService.getUserById(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
