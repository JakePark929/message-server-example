package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.KeyPrefix;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.User;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.projection.CountProjection;
import com.jake.messagesystem.dto.projection.UserIdUsernameProjection;
import com.jake.messagesystem.dto.projection.UsernameProjection;
import com.jake.messagesystem.repository.UserRepository;
import com.jake.messagesystem.util.JsonUtil;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final CacheService cacheService;
    private final UserRepository userRepository;

    private final JsonUtil jsonUtil;

    private final long TTL = 3600;
    private static final long LIMIT_FIND_COUNT = 100;

    public UserService(CacheService cacheService, UserRepository userRepository, JsonUtil jsonUtil) {
        this.cacheService = cacheService;
        this.userRepository = userRepository;
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
    public Pair<Map<UserId, String>, ResultType> getUsernames(Set<UserId> userIds) {
        if (userIds.size() > LIMIT_FIND_COUNT) {

            return Pair.of(Collections.emptyMap(), ResultType.OVER_LIMIT);
        }

        final List<String> usernames = cacheService.get(
                userIds.stream()
                        .map(userId -> cacheService.buildKey(KeyPrefix.USERNAME, userId.id().toString()))
                        .toList()
        );
        Map<UserId, String> resultMap = new HashMap<>();
        Set<UserId> missingUserIds = new HashSet<>();
        int index = 0;
        for (UserId userId : userIds) {
            String username = usernames.get(index++);
            if (username != null) {
                resultMap.put(userId, username);
            } else {
                missingUserIds.add(userId);
            }
        }

        if (!missingUserIds.isEmpty()) {
            final Map<UserId, String> userIdsAndUsernames = userRepository.findByUserIdIn(
                    missingUserIds.stream()
                            .map(UserId::id)
                            .collect(Collectors.toUnmodifiableSet())
            ).stream().collect(
                    Collectors.toMap(
                            projection ->
                                    new UserId(projection.getUserId()), UserIdUsernameProjection::getUsername)
            );

            resultMap.putAll(userIdsAndUsernames);
            cacheService.set(
                    userIdsAndUsernames.entrySet().stream().collect(
                            Collectors.toMap(
                                    entry -> cacheService.buildKey(
                                            KeyPrefix.USERNAME,
                                            entry.getKey().id().toString()
                                    ),
                                    Map.Entry::getValue
                            )
                    ),
                    TTL
            );
        }

        return Pair.of(resultMap, resultMap.isEmpty() ? ResultType.NOT_FOUND : ResultType.SUCCESS);
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
}
