package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.outbound.BaseMessage;
import com.jake.messagesystem.session.WebSocketSessionManager;
import com.jake.messagesystem.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Service
public class ClientNotificationService {
    private static final Logger log = LoggerFactory.getLogger(ClientNotificationService.class);

    private final WebSocketSessionManager webSocketSessionManager;
    private final PushService pushService;
    private final JsonUtil jsonUtil;

    public ClientNotificationService(WebSocketSessionManager webSocketSessionManager, PushService pushService, JsonUtil jsonUtil) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.pushService = pushService;
        this.jsonUtil = jsonUtil;

        pushService.registerPushMessageType(MessageType.INVITE_RESPONSE);
        pushService.registerPushMessageType(MessageType.ASK_INVITE);
        pushService.registerPushMessageType(MessageType.ACCEPT_RESPONSE);
        pushService.registerPushMessageType(MessageType.NOTIFY_ACCEPT);
        pushService.registerPushMessageType(MessageType.JOIN_RESPONSE);
        pushService.registerPushMessageType(MessageType.NOTIFY_JOIN);
        pushService.registerPushMessageType(MessageType.DISCONNECT_RESPONSE);
        pushService.registerPushMessageType(MessageType.REJECT_RESPONSE);
        pushService.registerPushMessageType(MessageType.CREATE_RESPONSE);
        pushService.registerPushMessageType(MessageType.QUIT_RESPONSE);
    }

    public void sendMessage(WebSocketSession session, UserId userId, BaseMessage message) {
        sendPayload(session, userId, message);
    }

    public void sendMessage(UserId userId, BaseMessage message) {
        sendPayload(webSocketSessionManager.getSession(userId), userId, message);
    }

    private void sendPayload(WebSocketSession session, UserId userId, BaseMessage message) {
        final Optional<String> json = jsonUtil.toJson(message);
        if (json.isEmpty()) {
            log.error("Send message failed. messageType: {}", message.getType());

            return;
        }

        String payload = json.get();
        try {
            if (session != null) {
                webSocketSessionManager.sendMessage(session, payload);
            } else {
                pushService.pushMessage(userId, message.getType(), payload);
            }
        } catch (Exception e) {
            pushService.pushMessage(userId, message.getType(), payload);
        }
    }
}
