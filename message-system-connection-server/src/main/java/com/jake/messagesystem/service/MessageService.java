package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.MessageNotificationRecord;
import com.jake.messagesystem.dto.websocket.outbound.MessageNotification;
import com.jake.messagesystem.session.WebSocketSessionManager;
import com.jake.messagesystem.util.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class MessageService {
    private final PushService pushService;

    private final WebSocketSessionManager webSocketSessionManager;
    private final JsonUtil jsonUtil;

    private static final int THREAD_POOL_SIZE = 10;
    private final ExecutorService senderThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public MessageService(PushService pushService, WebSocketSessionManager webSocketSessionManager, JsonUtil jsonUtil) {
        this.pushService = pushService;
        this.webSocketSessionManager = webSocketSessionManager;
        this.jsonUtil = jsonUtil;

        pushService.registerPushMessageType(MessageType.NOTIFY_MESSAGE, MessageNotificationRecord.class);
    }

    public void sendMessage(MessageNotificationRecord messageNotificationRecord) {
        Consumer<UserId> messageSender = (participantId) -> {
            WebSocketSession participantSession = webSocketSessionManager.getSession(participantId);
            final MessageNotification messageNotification = new MessageNotification(
                    messageNotificationRecord.channelId(),
                    messageNotificationRecord.messageSeqId(),
                    messageNotificationRecord.username(),
                    messageNotificationRecord.content()
            );

            if (participantSession != null) {
                jsonUtil.toJson(messageNotification).ifPresent(json -> {
                    try {
                        webSocketSessionManager.sendMessage(participantSession, json);
                    } catch (Exception e) {
                        pushService.pushMessage(messageNotificationRecord);
                    }
                });
            } else {
                pushService.pushMessage(messageNotificationRecord);
            }
        };

        messageNotificationRecord.participantIds().forEach(participantId ->
                CompletableFuture.runAsync(() -> messageSender.accept(participantId), senderThreadPool)
        );
    }
}
