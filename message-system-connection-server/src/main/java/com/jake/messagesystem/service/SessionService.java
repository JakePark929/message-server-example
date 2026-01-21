package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.KeyPrefix;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.kafka.ListenTopicCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final ListenTopicCreator listenTopicCreator;

    private final SessionRepository<? extends Session> httpSessionRepository;
    private final CacheService cacheService;

    private final long TTL = 300;

    public SessionService(ListenTopicCreator listenTopicCreator, SessionRepository<? extends Session> httpSessionRepository, CacheService cacheService) {
        this.listenTopicCreator = listenTopicCreator;
        this.httpSessionRepository = httpSessionRepository;
        this.cacheService = cacheService;
    }

    public void setOnline(UserId userId, boolean status) {
        final String key = buildUserLocationKey(userId);
        if (status) {
            cacheService.set(key, listenTopicCreator.getListenTopic(), TTL);
        } else {
            cacheService.delete(key);
        }
    }

    public boolean removeActiveChannel(UserId userId) {
        return cacheService.delete(buildChannelIdKey(userId));
    }

    public void refreshTTL(UserId userId, String httpSessionId) {
        try {
            final Session httpSession = httpSessionRepository.findById(httpSessionId);

            if (httpSession != null) {
                httpSession.setLastAccessedTime(Instant.now());
                cacheService.expire(buildChannelIdKey(userId), TTL);
                cacheService.expire(buildUserLocationKey(userId), TTL);
            }
        } catch (Exception e) {
            log.error("SQL find failed. httpSessionId: {}, cause: {}", httpSessionId, e.getMessage());
        }
    }

    private String buildChannelIdKey(UserId userId) {
        return cacheService.buildKey(KeyPrefix.USER, userId.id().toString(), IdKey.CHANNEL_ID.getValue());
    }

    private String buildUserLocationKey(UserId userId) {
        return cacheService.buildKey(KeyPrefix.USER_SESSION, userId.id().toString());
    }
}
