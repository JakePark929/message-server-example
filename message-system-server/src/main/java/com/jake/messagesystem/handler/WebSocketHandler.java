package com.jake.messagesystem.handler;

import com.jake.messagesystem.constants.Constants;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.BaseRequest;
import com.jake.messagesystem.handler.websocket.RequestDispatcher;
import com.jake.messagesystem.session.WebSocketSessionManager;
import com.jake.messagesystem.util.JsonUtil;
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
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    private final JsonUtil jsonUtil;
    private final WebSocketSessionManager webSocketSessionManager;
    private final RequestDispatcher requestDispatcher;

    public WebSocketHandler(JsonUtil jsonUtil, WebSocketSessionManager webSocketSessionManager, RequestDispatcher requestDispatcher) {
        this.jsonUtil = jsonUtil;
        this.webSocketSessionManager = webSocketSessionManager;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("ConnectionEstablished: {}", session.getId());

        ConcurrentWebSocketSessionDecorator concurrentWebSocketSessionDecorator = new ConcurrentWebSocketSessionDecorator(session, 5000, 100 * 1024);
        final UserId userId = (UserId) session.getAttributes().get(Constants.USER_ID.getValue());
        webSocketSessionManager.putSession(userId, concurrentWebSocketSessionDecorator);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        log.info("ConnectionClosed: [{}] from {}", status, session.getId());

        final UserId userId = (UserId) session.getAttributes().get(Constants.USER_ID.getValue());
        webSocketSessionManager.closeSession(userId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("TransportError: [{}] from {}", exception.getMessage(), session.getId());

        final UserId userId = (UserId) session.getAttributes().get(Constants.USER_ID.getValue());
        webSocketSessionManager.closeSession(userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession senderSession, TextMessage message) {
        String payload = message.getPayload();
        log.info("Received TextMessage: [{}] from {}", payload, senderSession.getId());

        jsonUtil.fromJson(payload, BaseRequest.class).ifPresent(msg -> requestDispatcher.dispatchRequest(senderSession, msg));
    }


}
