package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.KeyPrefix;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionService {
    private static final long TTL = 300;
    private final CacheService cacheService;

    public SessionService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public Optional<String> getListenTopic(UserId userId) {
        return cacheService.get(buildUserLocationKey(userId));
    }

    public Map<String, List<UserId>> getListenTopics(Collection<UserId> userIds) {
        final List<String> keys = userIds.stream().map(this::buildUserLocationKey).toList();
        final List<String> listenTopics = cacheService.get(keys);

        Map<String, List<UserId>> locationToUsers = new HashMap<>();
        final Iterator<String> iterator = listenTopics.iterator();
        for (UserId userId : userIds) {
            final String listenTopic = iterator.next();
            if (listenTopic != null) {
                locationToUsers.computeIfAbsent(listenTopic, unused -> new ArrayList<>()).add(userId);
            }
        }

        return locationToUsers;
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

    private String buildChannelIdKey(UserId userId) {
        return cacheService.buildKey(KeyPrefix.USER, userId.id().toString(), IdKey.CHANNEL_ID.getValue());
    }

    private String buildUserLocationKey(UserId userId) {
        return cacheService.buildKey(KeyPrefix.USER_SESSION, userId.id().toString());
    }
}
