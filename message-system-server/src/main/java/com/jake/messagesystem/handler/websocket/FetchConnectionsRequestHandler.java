package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.Connection;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.FetchConnectionsRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchConnectionsResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class FetchConnectionsRequestHandler implements BaseRequestHandler<FetchConnectionsRequest> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public FetchConnectionsRequestHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchConnectionsRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final List<Connection> connections = userConnectionService.getUsersByStatus(senderUserId, request.getStatus()).stream().map(user -> new Connection(user.username(), request.getStatus())).toList();
        clientNotificationService.sendMessage(senderSession, senderUserId, new FetchConnectionsResponse(connections));
    }
}
