package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.outbound.MessageNotificationRecord;
import com.jake.messagesystem.dto.websocket.outbound.BaseMessage;
import com.jake.messagesystem.entity.MessageEntity;
import com.jake.messagesystem.repository.MessageRepository;
import com.jake.messagesystem.session.WebSocketSessionManager;
import com.jake.messagesystem.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final JsonUtil jsonUtil;

    private final WebSocketSessionManager webSocketSessionManager;
    private final ChannelService channelService;
    private final PushService pushService;
    private final MessageRepository messageRepository;

    private static final int THREAD_POOL_SIZE = 10;
    private final ExecutorService senderThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public MessageService(JsonUtil jsonUtil, ChannelService channelService, PushService pushService, WebSocketSessionManager webSocketSessionManager, MessageRepository messageRepository) {
        this.jsonUtil = jsonUtil;
        this.channelService = channelService;
        this.pushService = pushService;
        this.webSocketSessionManager = webSocketSessionManager;
        this.messageRepository = messageRepository;

        pushService.registerPushMessageType(MessageType.NOTIFY_MESSAGE, MessageNotificationRecord.class);
    }

    @Transactional
    public void sendMessage(UserId senderUserId, String content, ChannelId channelId, MessageSeqId messageSeqId, BaseMessage message) {
        final Optional<String> json = jsonUtil.toJson(message);
        if (json.isEmpty()) {
            log.error("Send message failed. messageType: {}", message.getType());

            return;
        }

        String payload = json.get();
        try {
            messageRepository.save(new MessageEntity(channelId.id(), messageSeqId.id(), senderUserId.id(), content));
        } catch (Exception e) {
            log.error("Send message failed. cause: {}", e.getMessage());

            return;
        }

        final List<UserId> allParticipantIds = channelService.getParticipantIds(channelId);
        final List<UserId> onlineParticipantIds = channelService.getOnlineParticipantIds(channelId, allParticipantIds);
        for (int idx = 0; idx < allParticipantIds.size(); idx++) {
            final UserId participantId = allParticipantIds.get(idx);

            if (senderUserId.equals(participantId)) {
                continue;
            }

            if (onlineParticipantIds.get(idx) != null) {
                CompletableFuture.runAsync(() -> {
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
                }, senderThreadPool);
            } else {
                pushService.pushMessage(participantId, MessageType.NOTIFY_MESSAGE, payload);
            }
        }
    }
}
