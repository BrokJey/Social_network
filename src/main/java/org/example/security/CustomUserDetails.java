package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.example.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;
    
    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // пока без логики блокировки
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // пока без логики блокировки
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // пока без логики
    }

    @Override
    public boolean isEnabled() {
        return true; // можно завести флаг в User
    }

    public Long getId() {
        return user.getId();
    }
}
