package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.AcceptRequest;
import com.jake.messagesystem.dto.websocket.outbound.AcceptNotification;
import com.jake.messagesystem.dto.websocket.outbound.AcceptResponse;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.service.UserConnectionService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class AcceptRequestHandler implements BaseRequestHandler<AcceptRequest> {
    private final UserConnectionService userConnectionService;
    private final WebSocketSessionManager webSocketSessionManager;

    public AcceptRequestHandler(UserConnectionService userConnectionService, WebSocketSessionManager webSocketSessionManager) {
        this.userConnectionService = userConnectionService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, AcceptRequest request) {
        final UserId acceptorUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final Pair<Optional<UserId>, String> accept = userConnectionService.accept(acceptorUserId, request.getUsername());
        accept.getFirst().ifPresentOrElse(inviterUserId -> {
            webSocketSessionManager.sendMessage(senderSession, new AcceptResponse(request.getUsername()));
            String acceptUsername = accept.getSecond();
            webSocketSessionManager.sendMessage(webSocketSessionManager.getSession(inviterUserId), new AcceptNotification(acceptUsername));
        }, () -> {
            String errorMessage = accept.getSecond();
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.ACCEPT_REQUEST, errorMessage));
        });
    }
}
