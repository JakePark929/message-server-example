package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final SessionRepository<? extends Session> httpSessionRepository;
    private final StringRedisTemplate stringRedisTemplate;

    private final static String NAMESPACE = "message:user";
    private final long TTL = 300;

    public SessionService(SessionRepository<? extends Session> httpSessionRepository, StringRedisTemplate stringRedisTemplate) {
        this.httpSessionRepository = httpSessionRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String getUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }

    public List<UserId> getOnlineParticipantUserIds(ChannelId channelId, List<UserId> userIds) {
        final List<String> channelIdKeys = userIds.stream().map(this::buildChannelIdKey).toList();
        try {
            List<String> channelIds = stringRedisTemplate.opsForValue().multiGet(channelIdKeys);

            if (channelIds != null) {
                List<UserId> onlineParticipantUserIds = new ArrayList<>(channelIds.size());
                final String chId = channelId.id().toString();
                for (int idx = 0; idx < userIds.size(); idx++) {
                    String value = channelIds.get(idx);

                    if (value != null && value.equals(chId)) {
                        onlineParticipantUserIds.add(userIds.get(idx));
                    }
                }

                return onlineParticipantUserIds;
            }
        } catch (Exception e) {
            log.error("Redis mget failed. key: {}, cause: {}", channelIdKeys, e.getMessage());
        }

        return Collections.emptyList();
    }

    public boolean setActiveChannel(UserId userId, ChannelId channelId) {
        final String channelIdKey = buildChannelIdKey(userId);

        try {
            stringRedisTemplate.opsForValue().set(channelIdKey, channelId.id().toString(), TTL, TimeUnit.SECONDS);

            return true;
        } catch (Exception e) {
            log.error("Redis set failed. key: {}, channelId: {}", channelIdKey, channelId);

            return false;
        }
    }

    public void refreshTTL(UserId userId, String httpSessionId) {
        final String channelIdKey = buildChannelIdKey(userId);

        try {
            final Session httpSession = httpSessionRepository.findById(httpSessionId);

            if (httpSession != null) {
                httpSession.setLastAccessedTime(Instant.now());
                stringRedisTemplate.expire(channelIdKey, TTL, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("Redis expire failed. key: {}", channelIdKey);
        }
    }

    private String buildChannelIdKey(UserId userId) {
        return "%s:%d:%s".formatted(NAMESPACE, userId.id(), IdKey.CHANNEL_ID.getValue());
    }
}
