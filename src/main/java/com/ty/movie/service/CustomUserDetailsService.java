package com.ty.movie.service;

import com.ty.movie.model.User;
import com.ty.movie.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        User u = repo.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        // Normalize roles:
        // Accept "ROLE_USER" or "USER" and convert to authorities expected by Spring.
        String rawRole = u.getRole() == null ? "" : u.getRole().trim();

        // Support multiple roles separated by comma (e.g. "ROLE_ADMIN,ROLE_USER" or "ADMIN,USER")
        List<SimpleGrantedAuthority> authorities = Arrays.stream(rawRole.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    // if role is stored without prefix, add "ROLE_"
                    if (!s.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority("ROLE_" + s);
                    } else {
                        return new SimpleGrantedAuthority(s);
                    }
                })
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            // fallback to ROLE_USER to avoid granting nothing (optional)
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        log.debug("Loaded user '{}' with authorities={}", u.getUsername(),
                authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.joining(",")));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                authorities
        );
    }
}
