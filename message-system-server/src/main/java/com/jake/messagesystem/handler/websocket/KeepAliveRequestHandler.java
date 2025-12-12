package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.Constants;
import com.jake.messagesystem.dto.websocket.inbound.KeepAliveRequest;
import com.jake.messagesystem.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class KeepAliveRequestHandler implements BaseRequestHandler<KeepAliveRequest> {
    private final SessionService sessionService;

    public KeepAliveRequestHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, KeepAliveRequest request) {
        sessionService.refreshTTL((String) senderSession.getAttributes().get(Constants.HTTP_SESSION_ID.getValue()));
    }
}
