package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.KeyPrefix;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.User;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.projection.CountProjection;
import com.jake.messagesystem.dto.projection.UsernameProjection;
import com.jake.messagesystem.entity.UserEntity;
import com.jake.messagesystem.repository.UserRepository;
import com.jake.messagesystem.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    public static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final SessionService sessionService;
    private final CacheService cacheService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JsonUtil jsonUtil;

    private final long TTL = 3600;

    public UserService(SessionService sessionService, CacheService cacheService, UserRepository userRepository, PasswordEncoder passwordEncoder, JsonUtil jsonUtil) {
        this.sessionService = sessionService;
        this.cacheService = cacheService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jsonUtil = jsonUtil;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(InviteCode inviteCode) {
        final String key = cacheService.buildKey(KeyPrefix.USER, inviteCode.code());
        final Optional<String> cachedUser = cacheService.get(key);

        if (cachedUser.isPresent()) {

            return jsonUtil.fromJson(cachedUser.get(), User.class);
        }

        final Optional<User> fromDb = userRepository.findByInviteCode(inviteCode.code())
                .map(entity -> new User(new UserId(entity.getUserId()), entity.getUsername()));
        fromDb.flatMap(jsonUtil::toJson).ifPresent(json -> cacheService.set(key, json, TTL));

        return fromDb;
    }

    @Transactional(readOnly = true)
    public Optional<String> getUserName(UserId userId) {
        final String key = cacheService.buildKey(KeyPrefix.USERNAME, userId.id().toString());
        final Optional<String> cachedUsername = cacheService.get(key);

        if (cachedUsername.isPresent()) {

            return cachedUsername;
        }

        final Optional<String> fromDb = userRepository.findByUserId(userId.id()).map(UsernameProjection::getUsername);
        fromDb.ifPresent(username -> cacheService.set(key, username, TTL));

        return fromDb;
    }

    @Transactional(readOnly = true)
    public Optional<UserId> getUserId(String username) {
        final String key = cacheService.buildKey(KeyPrefix.USER_ID, username);
        final Optional<String> cachedUserId = cacheService.get(key);

        if (cachedUserId.isPresent()) {

            return Optional.of(new UserId(Long.valueOf(cachedUserId.get())));
        }

        final Optional<UserId> fromDb = userRepository.findUserIdByUsername(username).map(projection -> new UserId(projection.getUserId()));
        fromDb.ifPresent(userId -> cacheService.set(key, userId.id().toString(), TTL));

        return fromDb;
    }

    @Transactional(readOnly = true)
    public List<UserId> getUserIds(List<String> usernames) {

        return userRepository.findByUsernameIn(usernames).stream()
                .map(projection -> new UserId(projection.getUserId())).toList();
    }

    @Transactional(readOnly = true)
    public Optional<InviteCode> getInviteCode(UserId userId) {
        final String key = cacheService.buildKey(KeyPrefix.USER_INVITE_CODE, userId.id().toString());
        final Optional<String> cachedInviteCode = cacheService.get(key);

        if (cachedInviteCode.isPresent()) {

            return Optional.of(new InviteCode(cachedInviteCode.get()));
        }

        final Optional<InviteCode> fromDb = userRepository.findInviteCodeByUserId(userId.id())
                .map(inviteCode -> new InviteCode(inviteCode.getInviteCode()));
        fromDb.ifPresent(inviteCode -> cacheService.set(key, inviteCode.code(), TTL));

        return fromDb;
    }

    @Transactional(readOnly = true)
    public Optional<Integer> getConnectionCount(UserId userId) {
        return userRepository.findCountByUserId(userId.id()).map(CountProjection::getConnectionCount);
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
