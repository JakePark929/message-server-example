package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.entity.UserEntity;
import com.jake.messagesystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(SessionService sessionService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.sessionService = sessionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserId addUser(String username, String password) {
        final UserEntity userEntity = userRepository.save(new UserEntity(username, passwordEncoder.encode(password)));

        log.info("User registered. UserId: {}, Username: {}", userEntity.getUserId(), userEntity.getUsername());

        return new UserId(userEntity.getUserId());
    }

    @Transactional
    public void removeUser() {
        String username = sessionService.getUsername();
        final UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(userEntity.getUserId());

        log.info("User deleted. UserId: {}, Username: {}", userEntity.getUserId(), userEntity.getUsername());
    }
}
