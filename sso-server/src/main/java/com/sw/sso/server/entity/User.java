package com.sw.sso.server.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 11:32 上午
 * @Version:      1.0
 */
@Data
public class User implements UserDetails {

    private String name;

    private String password;

    private List<GrantedAuthority> roles;

    public User(String name, String password, List<GrantedAuthority> roles) {
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
