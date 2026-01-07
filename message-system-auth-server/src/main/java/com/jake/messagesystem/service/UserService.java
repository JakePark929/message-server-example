package com.jake.messagesystem.service;

import com.jake.messagesystem.constant.KeyPrefix;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.entity.UserEntity;
import com.jake.messagesystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    public static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final SessionService sessionService;
    private final CacheService cacheService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(SessionService sessionService, CacheService cacheService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.sessionService = sessionService;
        this.cacheService = cacheService;
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
        final UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        userRepository.deleteById(userEntity.getUserId());
        final String userId = userEntity.getUserId().toString();

        cacheService.delete(
                List.of(
                        cacheService.buildKey(KeyPrefix.USER_ID, username),
                        cacheService.buildKey(KeyPrefix.USERNAME, userId),
                        cacheService.buildKey(KeyPrefix.USER, userEntity.getInviteCode()),
                        cacheService.buildKey(KeyPrefix.USER_INVITE_CODE, userId)
                )
        );

        log.info("User deleted. UserId: {}, Username: {}", userEntity.getUserId(), userEntity.getUsername());
    }
}
