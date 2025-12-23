package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.DisconnectRequest;
import com.jake.messagesystem.dto.websocket.outbound.DisconnectResponse;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.service.UserConnectionService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class DisconnectRequestHandler implements BaseRequestHandler<DisconnectRequest> {
    private final UserConnectionService userConnectionService;
    private final WebSocketSessionManager webSocketSessionManager;

    public DisconnectRequestHandler(UserConnectionService userConnectionService, WebSocketSessionManager webSocketSessionManager) {
        this.userConnectionService = userConnectionService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, DisconnectRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
        final Pair<Boolean, String> disconnect = userConnectionService.disconnect(senderUserId, request.getUsername());

        if (disconnect.getFirst()) {
            webSocketSessionManager.sendMessage(senderSession, new DisconnectResponse(request.getUsername(), UserConnectionStatus.DISCONNECTED));
        } else {
            String errorMessage = disconnect.getSecond();
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.DISCONNECT_REQUEST, errorMessage));
        }
    }
}
