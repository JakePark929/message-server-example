package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.dto.websocket.inbound.WriteMessage;
import com.jake.messagesystem.dto.websocket.outbound.MessageNotification;
import com.jake.messagesystem.entity.MessageEntity;
import com.jake.messagesystem.repository.MessageRepository;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WriteMessageHandler implements BaseRequestHandler<WriteMessage> {
    private final WebSocketSessionManager webSocketSessionManager;
    private final MessageRepository messageRepository;

    public WriteMessageHandler(WebSocketSessionManager webSocketSessionManager, MessageRepository messageRepository) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.messageRepository = messageRepository;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, WriteMessage request) {
        MessageNotification receivedMessage = new MessageNotification(request.getUsername(), request.getContent());
        messageRepository.save(new MessageEntity(receivedMessage.getUsername(), receivedMessage.getContent()));

        webSocketSessionManager.getSessions().forEach(participantSession -> {
            if (!senderSession.getId().equals(participantSession.getId())) {
                webSocketSessionManager.sendMessage(participantSession, receivedMessage);
            }
        });
    }
}
