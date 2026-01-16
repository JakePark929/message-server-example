package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.outbound.MessageNotificationRecord;
import com.jake.messagesystem.dto.projection.MessageInfoProjection;
import com.jake.messagesystem.dto.websocket.outbound.BaseMessage;
import com.jake.messagesystem.dto.websocket.outbound.WriteMessageAck;
import com.jake.messagesystem.repository.UserChannelRepository;
import com.jake.messagesystem.session.WebSocketSessionManager;
import com.jake.messagesystem.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final UserService userService;
    private final ChannelService channelService;
    private final PushService pushService;
    private final MessageShardService messageShardService;
    private final UserChannelRepository userChannelRepository;

    private final WebSocketSessionManager webSocketSessionManager;
    private final JsonUtil jsonUtil;

    private static final int THREAD_POOL_SIZE = 10;
    private final ExecutorService senderThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public MessageService(UserService userService, ChannelService channelService, PushService pushService, MessageShardService messageShardService, UserChannelRepository userChannelRepository, WebSocketSessionManager webSocketSessionManager, JsonUtil jsonUtil) {
        this.userService = userService;
        this.channelService = channelService;
        this.pushService = pushService;
        this.messageShardService = messageShardService;
        this.userChannelRepository = userChannelRepository;
        this.webSocketSessionManager = webSocketSessionManager;
        this.jsonUtil = jsonUtil;

        pushService.registerPushMessageType(MessageType.NOTIFY_MESSAGE, MessageNotificationRecord.class);
    }

    @Transactional(readOnly = true)
    public Pair<List<Message>, ResultType> getMessages(ChannelId channelId, MessageSeqId startMessageSeqId, MessageSeqId endMessageSeqId) {
        final List<MessageInfoProjection> messageInfos = messageShardService.findByChannelIdAndMessageSequenceBetween(channelId, startMessageSeqId, endMessageSeqId);

        final Set<UserId> userIds = messageInfos.stream().map(projection ->
                new UserId(projection.getUserId())).collect(Collectors.toUnmodifiableSet()
        );
        if (userIds.isEmpty()) {

            return Pair.of(Collections.emptyList(), ResultType.SUCCESS);
        }

        final Pair<Map<UserId, String>, ResultType> result = userService.getUsernames(userIds);
        if (result.getSecond() == ResultType.SUCCESS) {
            final List<Message> messages = messageInfos.stream()
                    .map(projection -> {
                        final UserId userId = new UserId(projection.getUserId());

                        return new Message(
                                channelId,
                                new MessageSeqId(projection.getMessageSequence()),
                                result.getFirst().getOrDefault(userId, "unknown"),
                                projection.getContent()
                        );
                    }).toList();

            return Pair.of(messages, ResultType.SUCCESS);
        } else {
            return Pair.of(Collections.emptyList(), result.getSecond());
        }
    }

    @Transactional
    public void sendMessage(UserId senderUserId, String content, ChannelId channelId, MessageSeqId messageSeqId, Long serial, BaseMessage message) {
        final Optional<String> json = jsonUtil.toJson(message);
        if (json.isEmpty()) {
            log.error("Send message failed. messageType: {}", message.getType());

            return;
        }

        String payload = json.get();
        try {
            messageShardService.save(channelId, messageSeqId, senderUserId, content);
        } catch (Exception e) {
            log.error("Send message failed. cause: {}", e.getMessage());

            return;
        }

        final List<UserId> allParticipantIds = channelService.getParticipantIds(channelId);
        final List<UserId> onlineParticipantIds = channelService.getOnlineParticipantIds(channelId, allParticipantIds);
        for (int idx = 0; idx < allParticipantIds.size(); idx++) {
            final UserId participantId = allParticipantIds.get(idx);

            if (senderUserId.equals(participantId)) {
                updateLastReadMsgSeq(senderUserId, channelId, messageSeqId);
                jsonUtil.toJson(new WriteMessageAck(serial, messageSeqId)).ifPresent(writeMessageAck ->
                        CompletableFuture.runAsync(
                                () -> {
                                    try {
                                        WebSocketSession senderSession = webSocketSessionManager.getSession(senderUserId);
                                        if (senderSession != null) {
                                            webSocketSessionManager.sendMessage(senderSession, writeMessageAck);
                                        }
                                    } catch (Exception e) {
                                        log.warn("Send writeMessageAck failed. userId: {}, cause: {}", senderUserId.id(), e.getMessage());
                                    }
                                }, senderThreadPool
                        )
                );

                continue;
            }

            if (onlineParticipantIds.get(idx) != null) {
                CompletableFuture.runAsync(
                        () -> {
                            try {
                                final WebSocketSession session = webSocketSessionManager.getSession(participantId);

                                if (session != null) {
                                    webSocketSessionManager.sendMessage(session, payload);
                                } else {
                                    pushService.pushMessage(participantId, MessageType.NOTIFY_MESSAGE, payload);
                                }
                            } catch (Exception e) {
                                pushService.pushMessage(participantId, MessageType.NOTIFY_MESSAGE, payload);
                            }
                        }, senderThreadPool
                );
            } else {
                pushService.pushMessage(participantId, MessageType.NOTIFY_MESSAGE, payload);
            }
        }
    }

    @Transactional
    public void updateLastReadMsgSeq(UserId userId, ChannelId channelId, MessageSeqId messageSeqId) {
        if (userChannelRepository.updateLastReadMsgSeqByUserIdAndChannelId(userId.id(), channelId.id(), messageSeqId.id()) == 0) {
            log.error("Update lastReadMsgSeq failed. No record found for UserId: {} and ChannelId: {}", userId.id(), channelId.id());
        }
    }
}
