package com.ty.movie.service;

import com.ty.movie.model.User;
import com.ty.movie.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(User u) {
        u.setPassword(encoder.encode(u.getPassword()));
        u.setRole("ROLE_USER");
        return repo.save(u);
    }

    public boolean usernameExists(String username) {
        return repo.findByUsername(username).isPresent();
    }

    public Optional<User> findByUsername(String username){
        return repo.findByUsername(username);
    }
}
