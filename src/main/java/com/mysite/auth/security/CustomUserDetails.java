package com.mysite.auth.security;

import com.mysite.auth.domain.entity.User;
import com.mysite.auth.domain.enums.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final String provider;
    private final UserRole role;

    public CustomUserDetails(String email, String password, String provider, UserRole role) {
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.role = role;
    }

    public static CustomUserDetails from(User user) {
        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                user.getProvider().name(),
                user.getRole()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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
