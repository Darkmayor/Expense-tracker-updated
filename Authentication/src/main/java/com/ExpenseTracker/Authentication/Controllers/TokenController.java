package com.ExpenseTracker.Authentication.Controllers;

import com.ExpenseTracker.Authentication.Entities.RefreshToken;
import com.ExpenseTracker.Authentication.Entities.UserInfo;
import com.ExpenseTracker.Authentication.Request.AuthRequestDto;
import com.ExpenseTracker.Authentication.Request.RefreshTokenRequestDto;
import com.ExpenseTracker.Authentication.Response.JwtResponseDTO;
import com.ExpenseTracker.Authentication.Services.JwtService;
import com.ExpenseTracker.Authentication.Services.RefreshTokenService;
import com.ExpenseTracker.Authentication.Services.UserDetailServiceImpl;
import com.ExpenseTracker.Authentication.Entities.UserRoles;
import com.ExpenseTracker.Authentication.model.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @PostMapping("/auth/v1/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            String fetchedUserId = userDetailService.getUserByUsername(authRequestDTO.getUsername());

            if (Objects.nonNull(fetchedUserId) && Objects.nonNull(refreshToken)) {
                List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
                return new ResponseEntity<>(JwtResponseDTO.builder()
                        .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername(), roles))
                        .token(refreshToken.getToken())
                        .userId(fetchedUserId)
                        .roles(roles)
                        .build(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/auth/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    List<String> roles = userInfo.getRoles().stream().map(UserRoles::getName).collect(Collectors.toList());
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername(), roles);
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken())
                            .roles(roles)
                            .build();
                }).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    }

    @PostMapping("/auth/v1/signup")
    public ResponseEntity signup(@RequestBody UserInfoDto userInfoDto) {
        try {
            UserInfo userInfo = userDetailService.signupUser(userInfoDto);
            if (Objects.nonNull(userInfo)) {
                return new ResponseEntity<>("User Shared Successfully", HttpStatus.OK);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("User already exist", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/auth/v1/ping")
    public ResponseEntity<String> ping(Authentication authentication) {
        String username = authentication.getName();
        String userId = userDetailService.getUserByUsername(username);
        return new ResponseEntity<>(userId, HttpStatus.OK);
    }
}
