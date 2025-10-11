package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.entity.MessageUserEntity;
import com.jake.messagesystem.repository.MessageUserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MessageUserService {
    public static final Logger log = LoggerFactory.getLogger(MessageUserService.class);

    private final SessionService sessionService;
    private final MessageUserRepository messageUserRepository;
    private final PasswordEncoder passwordEncoder;

    public MessageUserService(SessionService sessionService, MessageUserRepository messageUserRepository, PasswordEncoder passwordEncoder) {
        this.sessionService = sessionService;
        this.messageUserRepository = messageUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserId addUser(String username, String password) {
        final MessageUserEntity messageUserEntity = messageUserRepository.save(new MessageUserEntity(username, passwordEncoder.encode(password)));

        log.info("User registered. UserId: {}, Username: {}", messageUserEntity.getUserId(), messageUserEntity.getUsername());

        return new UserId(messageUserEntity.getUserId());
    }

    @Transactional
    public void removeUser() {
        String username = sessionService.getUsername();
        final MessageUserEntity messageUserEntity = messageUserRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        messageUserRepository.deleteById(messageUserEntity.getUserId());

        log.info("User deleted. UserId: {}, Username: {}", messageUserEntity.getUserId(), messageUserEntity.getUsername());
    }
}
