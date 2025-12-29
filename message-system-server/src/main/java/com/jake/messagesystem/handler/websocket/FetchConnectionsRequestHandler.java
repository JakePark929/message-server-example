package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.Connection;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.FetchConnectionsRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchConnectionsResponse;
import com.jake.messagesystem.service.UserConnectionService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class FetchConnectionsRequestHandler implements BaseRequestHandler<FetchConnectionsRequest> {
    private final UserConnectionService userConnectionService;
    private final WebSocketSessionManager webSocketSessionManager;

    public FetchConnectionsRequestHandler(UserConnectionService userConnectionService, WebSocketSessionManager webSocketSessionManager) {
        this.userConnectionService = userConnectionService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchConnectionsRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final List<Connection> connections = userConnectionService.getUsersByStatus(senderUserId, request.getStatus()).stream().map(user -> new Connection(user.username(), request.getStatus())).toList();
        webSocketSessionManager.sendMessage(senderSession, new FetchConnectionsResponse(connections));
    }
}
