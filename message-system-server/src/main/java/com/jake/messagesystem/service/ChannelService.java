package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.Channel;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.projection.ChannelTitleProjection;
import com.jake.messagesystem.entity.ChannelEntity;
import com.jake.messagesystem.entity.UserChannelEntity;
import com.jake.messagesystem.repository.ChannelRepository;
import com.jake.messagesystem.repository.UserChannelRepository;
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

    private final SessionService sessionService;
    private final UserConnectionService userConnectionService;
    private final ChannelRepository channelRepository;
    private final UserChannelRepository userChannelRepository;

    private static final int LIMIT_HEAD_COUNT = 100;

    public ChannelService(SessionService sessionService, UserConnectionService userConnectionService, ChannelRepository channelRepository, UserChannelRepository userChannelRepository) {
        this.sessionService = sessionService;
        this.userConnectionService = userConnectionService;
        this.channelRepository = channelRepository;
        this.userChannelRepository = userChannelRepository;
    }

    public Optional<InviteCode> getInviteCode(ChannelId channelId) {
        final Optional<InviteCode> inviteCode = channelRepository.findChannelInviteCodeByChannelId(channelId.id())
                .map(projection -> new InviteCode(projection.getInviteCode()));

        if (inviteCode.isEmpty()) {
            log.warn("Invite code is not exist. channelId: {}", channelId);
        }

        return inviteCode;
    }

    public boolean isJoined(ChannelId channelId, UserId userId) {
        return userChannelRepository.existsByUserIdAndChannelId(userId.id(), channelId.id());
    }

    public List<UserId> getParticipantIds(ChannelId channelId) {
        return userChannelRepository.findUserIdsByChannelId(channelId.id()).stream()
                .map(userId -> new UserId(userId.getUserId()))
                .toList();
    }

    public List<UserId> getOnlineParticipantIds(ChannelId channelId) {
        return sessionService.getOnlineParticipantUserIds(channelId, getParticipantIds(channelId));
    }

    public List<Channel> getChannels(UserId userId) {
        return userChannelRepository.findChannelsByUserId(userId.id()).stream()
                .map(projection -> new Channel(new ChannelId(projection.getChannelId()), projection.getTitle(), projection.getHeadCount()))
                .toList();
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

    public Pair<Optional<String>, ResultType> enter(ChannelId channelId, UserId userId) {
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

        boolean activated = sessionService.setActiveChannel(userId, channelId);
        if (!activated) {
            log.error("Enter channel failed. Session update failed. channelId: {}, userId: {}", channelId, userId);
            return Pair.of(Optional.empty(), ResultType.FAILED);
        }

        return Pair.of(title, ResultType.SUCCESS);
    }
}
