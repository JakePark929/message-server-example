package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.WriteMessage;
import com.jake.messagesystem.dto.websocket.outbound.MessageNotification;
import com.jake.messagesystem.service.MessageService;
import com.jake.messagesystem.service.UserService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WriteMessageHandler implements BaseRequestHandler<WriteMessage> {
    private final UserService userService;
    private final MessageService messageService;
    private final WebSocketSessionManager webSocketSessionManager;

    public WriteMessageHandler(UserService userService, MessageService messageService, WebSocketSessionManager webSocketSessionManager) {
        this.userService = userService;
        this.messageService = messageService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, WriteMessage request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
        ChannelId channelId = request.getChannelId();
        String content = request.getContent();
        String senderUsername = userService.getUserName(senderUserId).orElse("unknown");

        messageService.sendMessage(senderUserId, content, channelId, (participantId) -> {
            final WebSocketSession participantSession = webSocketSessionManager.getSession(participantId);
            MessageNotification messageNotification = new MessageNotification(channelId, senderUsername, content);

            if (participantSession != null) {
                webSocketSessionManager.sendMessage(participantSession, messageNotification);
            }
        });
    }
}
