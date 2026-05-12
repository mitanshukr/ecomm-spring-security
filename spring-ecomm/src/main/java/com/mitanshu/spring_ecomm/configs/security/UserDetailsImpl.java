package com.mitanshu.spring_ecomm.configs.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mitanshu.spring_ecomm.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {
    Long id;
    String username;
    String name;
    String email;
    @JsonIgnore
    String password;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user){
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName().name());

        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getName(),
                user.getPassword(), user.getPassword(), List.of(authority));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
