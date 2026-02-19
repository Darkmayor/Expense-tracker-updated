package com.ExpenseTracker.Authentication.Services;

import com.ExpenseTracker.Authentication.Entities.UserInfo;
import com.ExpenseTracker.Authentication.Entities.UserRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetailService extends UserInfo implements UserDetails {


    private String username;
    private String password;
    Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetailService(UserInfo userInfo){
        this.username = userInfo.getUsername();
        this.password = userInfo.getPassword();

        List<GrantedAuthority> auths = new ArrayList<>();
        //iterate over userInfo's roles
        for(UserRoles role: userInfo.getRoles()){
            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
        }
        this.authorities = auths;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
