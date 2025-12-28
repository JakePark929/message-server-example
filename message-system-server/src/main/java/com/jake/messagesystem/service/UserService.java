package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.User;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.projection.CountProjection;
import com.jake.messagesystem.dto.projection.UsernameProjection;
import com.jake.messagesystem.entity.UserEntity;
import com.jake.messagesystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<String> getUserName(UserId userId) {
        return userRepository.findByUserId(userId.id()).map(UsernameProjection::getUsername);
    }

    public Optional<UserId> getUserId(String username) {
        return userRepository.findByUsername(username).map(userEntity -> new UserId(userEntity.getUserId()));
    }

    public List<UserId> getUserIds(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames).stream()
                .map(projection -> new UserId(projection.getUserId())).toList();
    }

    public Optional<User> getUser(InviteCode inviteCode) {
        return userRepository.findByInviteCode(inviteCode.code())
                .map(entity -> new User(new UserId(entity.getUserId()), entity.getUsername()));
    }

    public Optional<InviteCode> getInviteCode(UserId userId) {
        return userRepository.findInviteCodeByUserId(userId.id()).map(inviteCode -> new InviteCode(inviteCode.getInviteCode()));
    }

    @Transactional
    public UserId addUser(String username, String password) {
        final UserEntity userEntity = userRepository.save(new UserEntity(username, passwordEncoder.encode(password)));

        log.info("User registered. UserId: {}, Username: {}", userEntity.getUserId(), userEntity.getUsername());

        return new UserId(userEntity.getUserId());
    }

    public Optional<Integer> getConnectionCount(UserId userId) {
        return userRepository.findCountByUserId(userId.id()).map(CountProjection::getConnectionCount);
    }

    @Transactional
    public void removeUser() {
        String username = sessionService.getUsername();
        final UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(userEntity.getUserId());

        log.info("User deleted. UserId: {}, Username: {}", userEntity.getUserId(), userEntity.getUsername());
    }
}
