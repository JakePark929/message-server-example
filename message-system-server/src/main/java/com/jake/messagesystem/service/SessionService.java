package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.KeyPrefix;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final SessionRepository<? extends Session> httpSessionRepository;
    private final CacheService cacheService;

    private final long TTL = 300;

    public SessionService(SessionRepository<? extends Session> httpSessionRepository, CacheService cacheService) {
        this.httpSessionRepository = httpSessionRepository;
        this.cacheService = cacheService;
    }

    public List<UserId> getOnlineParticipantUserIds(ChannelId channelId, List<UserId> userIds) {
        final List<String> channelIdKeys = userIds.stream().map(this::buildChannelIdKey).toList();
        final List<String> channelIds = cacheService.get(channelIdKeys);

        if (channelIds != null) {
            List<UserId> onlineParticipantUserIds = new ArrayList<>(channelIds.size());
            final String chId = channelId.id().toString();
            for (int idx = 0; idx < userIds.size(); idx++) {
                String value = channelIds.get(idx);
                onlineParticipantUserIds.add(value != null && value.equals(chId) ? userIds.get(idx) : null);
            }

            return onlineParticipantUserIds;
        }

        return Collections.emptyList();
    }

    public boolean setActiveChannel(UserId userId, ChannelId channelId) {

        return cacheService.set(buildChannelIdKey(userId), channelId.id().toString(), TTL);
    }

    public boolean removeActiveChannel(UserId userId) {

        return cacheService.delete(buildChannelIdKey(userId));
    }

    public void refreshTTL(UserId userId, String httpSessionId) {
        final String channelIdKey = buildChannelIdKey(userId);

        try {
            final Session httpSession = httpSessionRepository.findById(httpSessionId);

            if (httpSession != null) {
                httpSession.setLastAccessedTime(Instant.now());
                cacheService.expire(channelIdKey, TTL);
            }
        } catch (Exception e) {
            log.error("SQL find failed. httpSessionId: {}, cause: {}", httpSessionId, e.getMessage());
        }
    }

    private String buildChannelIdKey(UserId userId) {

        return cacheService.buildKey(KeyPrefix.USER, userId.id().toString(), IdKey.CHANNEL_ID.getValue());
    }
}
