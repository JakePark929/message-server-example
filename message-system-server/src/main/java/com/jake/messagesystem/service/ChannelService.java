package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.KeyPrefix;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.Channel;
import com.jake.messagesystem.dto.ChannelEntry;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.projection.ChannelTitleProjection;
import com.jake.messagesystem.entity.ChannelEntity;
import com.jake.messagesystem.entity.UserChannelEntity;
import com.jake.messagesystem.repository.ChannelRepository;
import com.jake.messagesystem.repository.UserChannelRepository;
import com.jake.messagesystem.util.JsonUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChannelService {
    private static final Logger log = LoggerFactory.getLogger(ChannelService.class);

    private static final long TTL = 600;
    private final SessionService sessionService;
    private final UserConnectionService userConnectionService;
    private final MessageShardService messageShardService;
    private final ChannelRepository channelRepository;
    private final UserChannelRepository userChannelRepository;
    private final CacheService cacheService;

    private static final int LIMIT_HEAD_COUNT = 100;
    private final JsonUtil jsonUtil;

    public ChannelService(CacheService cacheService, SessionService sessionService, UserConnectionService userConnectionService, MessageShardService messageShardService, ChannelRepository channelRepository, UserChannelRepository userChannelRepository, JsonUtil jsonUtil) {
        this.cacheService = cacheService;
        this.sessionService = sessionService;
        this.userConnectionService = userConnectionService;
        this.messageShardService = messageShardService;
        this.channelRepository = channelRepository;
        this.userChannelRepository = userChannelRepository;
        this.jsonUtil = jsonUtil;
    }

    @Transactional(readOnly = true)
    public Optional<Channel> getChannel(InviteCode inviteCode) {
        final String key = cacheService.buildKey(KeyPrefix.CHANNEL, inviteCode.code());
        final Optional<String> cachedChannel = cacheService.get(key);

        if (cachedChannel.isPresent()) {

            return jsonUtil.fromJson(cachedChannel.get(), Channel.class);
        }

        final Optional<Channel> fromDb = channelRepository.findChannelByInviteCode(inviteCode.code())
                .map(projection -> new Channel(new ChannelId(projection.getChannelId()), projection.getTitle(), projection.getHeadCount()));
        fromDb.flatMap(jsonUtil::toJson).ifPresent(json -> cacheService.set(key, json, TTL));

        return fromDb;
    }

    @Transactional(readOnly = true)
    public List<Channel> getChannels(UserId userId) {
        final String key = cacheService.buildKey(KeyPrefix.CHANNELS, userId.id().toString());
        final Optional<String> cachedChannels = cacheService.get(key);

        if (cachedChannels.isPresent()) {

            return jsonUtil.fromJsonToList(cachedChannels.get(), Channel.class);
        }

        final List<Channel> fromDb = userChannelRepository.findChannelsByUserId(userId.id()).stream()
                .map(projection -> new Channel(new ChannelId(projection.getChannelId()), projection.getTitle(), projection.getHeadCount()))
                .toList();

        if (!fromDb.isEmpty()) {
            jsonUtil.toJson(fromDb).ifPresent(json -> cacheService.set(key, json, TTL));
        }

        return fromDb;
    }

    @Transactional(readOnly = true)
    public Optional<InviteCode> getInviteCode(ChannelId channelId) {
        final String key = cacheService.buildKey(KeyPrefix.CHANNEL_INVITE_CODE, channelId.id().toString());
        final Optional<String> cachedInviteCode = cacheService.get(key);

        if (cachedInviteCode.isPresent()) {

            return Optional.of(new InviteCode(cachedInviteCode.get()));
        }

        final Optional<InviteCode> fromDb = channelRepository.findChannelInviteCodeByChannelId(channelId.id())
                .map(projection -> new InviteCode(projection.getInviteCode()));

        if (fromDb.isEmpty()) {
            log.warn("Invite code is not exist. channelId: {}", channelId);
        }

        fromDb.ifPresent(inviteCode -> cacheService.set(key, inviteCode.code(), TTL));

        return fromDb;
    }

    @Transactional(readOnly = true)
    public boolean isJoined(ChannelId channelId, UserId userId) {
        final String key = cacheService.buildKey(KeyPrefix.JOINED_CHANNEL, channelId.id().toString(), userId.id().toString());
        final Optional<String> cachedChannel = cacheService.get(key);

        if (cachedChannel.isPresent()) {

            return true;
        }

        final boolean fromDb = userChannelRepository.existsByUserIdAndChannelId(userId.id(), channelId.id());

        if (fromDb) {
            cacheService.set(key, "T", TTL);
        }

        return fromDb;
    }

    @Transactional(readOnly = true)
    public List<UserId> getParticipantIds(ChannelId channelId) {
        final String key = cacheService.buildKey(KeyPrefix.PARTICIPANT_IDS, channelId.id().toString());
        final Optional<String> cachedParticipantIds = cacheService.get(key);
        if (cachedParticipantIds.isPresent()) {

            return jsonUtil.fromJsonToList(cachedParticipantIds.get(), String.class).stream()
                    .map(userId -> new UserId(Long.valueOf(userId)))
                    .toList();
        }

        final List<UserId> fromDb = userChannelRepository.findUserIdsByChannelId(channelId.id()).stream()
                .map(userId -> new UserId(userId.getUserId()))
                .toList();

        if (!fromDb.isEmpty()) {
            jsonUtil.toJson(fromDb.stream().map(UserId::id).toList()).ifPresent(json -> cacheService.set(key, json, TTL));
        }

        return fromDb;
    }

    public List<UserId> getOnlineParticipantIds(ChannelId channelId, List<UserId> userIds) {
        return sessionService.getOnlineParticipantUserIds(channelId, userIds);
    }

    @Transactional
    public Pair<Optional<Channel>, ResultType> create(UserId senderUSerId, List<UserId> participantIds, String title) {
        if (title == null || title.isEmpty()) {
            log.warn("Invalid args : title is empty.");

            return Pair.of(Optional.empty(), ResultType.INVALID_ARGS);
        }

        int headCount = participantIds.size() + 1;
        if (headCount > LIMIT_HEAD_COUNT) {
            log.warn("Over limit of channel. senderUserId: {}, participantIds count={}, title={}", senderUSerId, participantIds.size(), title);

            return Pair.of(Optional.empty(), ResultType.OVER_LIMIT);
        }

        if (userConnectionService.countConnectionStatus(senderUSerId, participantIds, UserConnectionStatus.ACCEPTED) != participantIds.size()) {
            log.warn("Included unconnected user. participantIds: {}", participantIds);

            return Pair.of(Optional.empty(), ResultType.NOT_ALLOWED);
        }

        try {
            ChannelEntity channelEntity = channelRepository.save(new ChannelEntity(title, headCount));
            final Long channelId = channelEntity.getChannelId();

            final List<UserChannelEntity> userChannelEntities = participantIds.stream().map(participantId -> new UserChannelEntity(participantId.id(), channelId, 0)).collect(Collectors.toList());
            userChannelEntities.add(new UserChannelEntity(senderUSerId.id(), channelId, 0));
            userChannelRepository.saveAll(userChannelEntities);
            Channel channel = new Channel(new ChannelId(channelId), title, headCount);

            return Pair.of(Optional.of(channel), ResultType.SUCCESS);
        } catch (Exception e) {
            log.error("Create failed. cause: {}", e.getMessage());

            throw e;
        }
    }

    @Transactional
    public Pair<Optional<Channel>, ResultType> join(InviteCode inviteCode, UserId userId) {
        final Optional<Channel> ch = getChannel(inviteCode);
        if (ch.isEmpty()) {

            return Pair.of(Optional.empty(), ResultType.NOT_FOUND);
        }

        final Channel channel = ch.get();
        if (isJoined(channel.channelId(), userId)) {

            return Pair.of(Optional.empty(), ResultType.ALREADY_JOINED);
        } else if (channel.headCount() >= LIMIT_HEAD_COUNT) {

            return Pair.of(Optional.empty(), ResultType.OVER_LIMIT);
        }

        final ChannelEntity channelEntity = channelRepository.findForUpdateByChannelId(channel.channelId().id())
                .orElseThrow(() -> new EntityNotFoundException("Invalid channelId: " + channel.channelId().id()));

        if (channelEntity.getHeadCount() < LIMIT_HEAD_COUNT) {
            channelEntity.setHeadCount(channelEntity.getHeadCount() + 1);
            userChannelRepository.save(new UserChannelEntity(userId.id(), channel.channelId().id(), 0));
            cacheService.delete(
                    List.of(
                            cacheService.buildKey(KeyPrefix.CHANNEL, channelEntity.getInviteCode()),
                            cacheService.buildKey(KeyPrefix.CHANNELS, userId.id().toString())
                    )
            );
        }

        return Pair.of(Optional.of(channel), ResultType.SUCCESS);
    }

    @Transactional(readOnly = true)
    public Pair<Optional<ChannelEntry>, ResultType> enter(ChannelId channelId, UserId userId) {
        if (!isJoined(channelId, userId)) {
            log.warn("Enter channel failed. User not joined. channelId: {}, userId: {}", channelId, userId);

            return Pair.of(Optional.empty(), ResultType.NOT_JOINED);
        }

        Optional<String> title =
                channelRepository.findChanelTitleByChannelId(channelId.id())
                        .map(ChannelTitleProjection::getTitle);

        if (title.isEmpty()) {
            log.warn("Enter channel failed. Channel does not exist. channelId: {}, userId: {}", channelId, userId);

            return Pair.of(Optional.empty(), ResultType.NOT_FOUND);
        }

        final Optional<MessageSeqId> lastReadMsgSeq = userChannelRepository.findLastReadMsgSeqByUserIdAndChannelId(userId.id(), channelId.id()).map(lastReadMsgSeqProjection -> new MessageSeqId(lastReadMsgSeqProjection.getLastReadMsgSeq()));

        if (lastReadMsgSeq.isEmpty()) {
            log.error("Enter channel failed. No record found for userId: {}, channelId: {}", userId.id(), channelId.id());

            return Pair.of(Optional.empty(), ResultType.NOT_FOUND);
        }

        final MessageSeqId lastChannelmessageSeqId = messageShardService.findLastMessageSequenceByChannelId(channelId);

        if (sessionService.setActiveChannel(userId, channelId)) {

            return Pair.of(Optional.of(new ChannelEntry(title.get(), lastReadMsgSeq.get(), lastChannelmessageSeqId)), ResultType.SUCCESS);
        }

        log.error("Enter channel failed. Session update failed. channelId: {}, userId: {}", channelId, userId);

        return Pair.of(Optional.empty(), ResultType.FAILED);
    }

    public boolean leave(UserId userId) {
        return sessionService.removeActiveChannel(userId);
    }

    @Transactional
    public ResultType quit(ChannelId channelId, UserId userId) {
        if (!isJoined(channelId, userId)) {

            return ResultType.NOT_JOINED;
        }

        final ChannelEntity channelEntity = channelRepository.findForUpdateByChannelId(channelId.id())
                .orElseThrow(() -> new EntityNotFoundException("Invalid channelId: " + channelId.id()));

        if (channelEntity.getHeadCount() > 0) {
            channelEntity.setHeadCount(channelEntity.getHeadCount() - 1);
        } else {
            log.error("Count is already zero. channelId: {}, userId: {}", channelId, userId);
        }

        userChannelRepository.deleteByUserIdAndChannelId(userId.id(), channelId.id());
        cacheService.delete(
                List.of(
                        cacheService.buildKey(KeyPrefix.CHANNEL, channelEntity.getInviteCode()),
                        cacheService.buildKey(KeyPrefix.CHANNELS, userId.id().toString())
                )
        );

        return ResultType.SUCCESS;
    }
}
