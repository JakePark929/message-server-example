package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.AcceptNotificationRecord;
import com.jake.messagesystem.dto.kafka.AcceptResponseRecord;
import com.jake.messagesystem.dto.kafka.CreateResponseRecord;
import com.jake.messagesystem.dto.kafka.DisconnectResponseRecord;
import com.jake.messagesystem.dto.kafka.InviteNotificationRecord;
import com.jake.messagesystem.dto.kafka.InviteResponseRecord;
import com.jake.messagesystem.dto.kafka.JoinNotificationRecord;
import com.jake.messagesystem.dto.kafka.LeaveResponseRecord;
import com.jake.messagesystem.dto.kafka.QuitResponseRecord;
import com.jake.messagesystem.dto.kafka.RecordInterface;
import com.jake.messagesystem.dto.kafka.RejectResponseRecord;
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

        pushService.registerPushMessageType(MessageType.INVITE_RESPONSE, InviteResponseRecord.class);
        pushService.registerPushMessageType(MessageType.ASK_INVITE, InviteNotificationRecord.class);
        pushService.registerPushMessageType(MessageType.ACCEPT_RESPONSE, AcceptResponseRecord.class);
        pushService.registerPushMessageType(MessageType.NOTIFY_ACCEPT, AcceptNotificationRecord.class);
        pushService.registerPushMessageType(MessageType.NOTIFY_JOIN, JoinNotificationRecord.class);
        pushService.registerPushMessageType(MessageType.DISCONNECT_RESPONSE, DisconnectResponseRecord.class);
        pushService.registerPushMessageType(MessageType.REJECT_RESPONSE, RejectResponseRecord.class);
        pushService.registerPushMessageType(MessageType.CREATE_RESPONSE, CreateResponseRecord.class);
        pushService.registerPushMessageType(MessageType.LEAVE_RESPONSE, LeaveResponseRecord.class);
        pushService.registerPushMessageType(MessageType.QUIT_RESPONSE, QuitResponseRecord.class);
    }

    public void sendErrorMessage(WebSocketSession session, BaseMessage message) {
        sendPayload(session, message, null);
    }

    public void sendMessage(UserId userId, BaseMessage message, RecordInterface recordInterface) {
        sendPayload(webSocketSessionManager.getSession(userId), message, recordInterface);
    }

    private void sendPayload(WebSocketSession session, BaseMessage message, RecordInterface recordInterface) {
        final Optional<String> json = jsonUtil.toJson(message);
        if (json.isEmpty()) {
            log.error("Send message failed. messageType: {}", message.getType());

            return;
        }

        String payload = json.get();
        final Runnable pushMessage = () -> {
            if (recordInterface != null) {
                pushService.pushMessage(recordInterface);
            }
        };

        try {
            if (session != null) {
                webSocketSessionManager.sendMessage(session, payload);
            } else {
                pushMessage.run();
            }
        } catch (Exception e) {
            pushMessage.run();
        }
    }
}
