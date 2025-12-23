package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.KeepAlive;
import com.jake.messagesystem.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class KeepAliveHandler implements BaseRequestHandler<KeepAlive> {
    private final SessionService sessionService;

    public KeepAliveHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, KeepAlive request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        sessionService.refreshTTL(senderUserId, (String) senderSession.getAttributes().get(IdKey.HTTP_SESSION_ID.getValue()));
    }
}
