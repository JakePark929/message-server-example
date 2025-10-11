package com.jake.messagesystem.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jake.messagesystem.constants.Constants;
import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.dto.websocket.inbound.BaseRequest;
import com.jake.messagesystem.dto.websocket.inbound.KeepAliveRequest;
import com.jake.messagesystem.dto.websocket.inbound.MessageRequest;
import com.jake.messagesystem.entity.MessageEntity;
import com.jake.messagesystem.repository.MessageRepository;
import com.jake.messagesystem.service.SessionService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MessageHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketSessionManager webSocketSessionManager;
    private final SessionService sessionService;
    private final MessageRepository messageRepository;

    public MessageHandler(WebSocketSessionManager webSocketSessionManager, SessionService sessionService, MessageRepository messageRepository) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.sessionService = sessionService;
        this.messageRepository = messageRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("ConnectionEstablished: {}", session.getId());

        ConcurrentWebSocketSessionDecorator concurrentWebSocketSessionDecorator = new ConcurrentWebSocketSessionDecorator(session, 5000, 100 * 1024);

        webSocketSessionManager.storeSession(concurrentWebSocketSessionDecorator);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        log.info("ConnectionClosed: [{}] from {}", status, session.getId());

        webSocketSessionManager.terminateSession(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("TransportError: [{}] from {}", exception.getMessage(), session.getId());

        webSocketSessionManager.terminateSession(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession senderSession, TextMessage message) {
        String payload = message.getPayload();

        log.info("Received TextMessage: [{}] from {}", payload, senderSession.getId());

        try {
            final BaseRequest baseRequest = objectMapper.readValue(payload, BaseRequest.class);

            if (baseRequest instanceof MessageRequest messageRequest) {
                Message recievedMessage = new Message(messageRequest.getUsername(), messageRequest.getContent());
                messageRepository.save(new MessageEntity(recievedMessage.username(), recievedMessage.content()));

                webSocketSessionManager.getSessions().forEach(participantSession -> {
                    if (!senderSession.getId().equals(participantSession.getId())) {
                        sendMessage(participantSession, recievedMessage);
                    }
                });
            } else if (baseRequest instanceof KeepAliveRequest) {
                sessionService.refreshTTL((String) senderSession.getAttributes().get(Constants.HTTP_SESSION_ID.getValue()));
            }
        } catch (Exception e) {
            String errorMessage = "유효한 프로토콜이 아닙니다.";

            log.error("errorMessage payload: {} from {}", payload, senderSession.getId());
            sendMessage(senderSession, new Message("system", errorMessage));
        }
    }

    private void sendMessage(WebSocketSession session, Message message) {
        try {
            String msg = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(msg));
            log.info("send message: {} to {}", msg, session.getId());
        } catch (Exception e) {
            log.error("메시지 전송 실패 to {} error: {}", session.getId(), e.getMessage());
        }
    }
}
