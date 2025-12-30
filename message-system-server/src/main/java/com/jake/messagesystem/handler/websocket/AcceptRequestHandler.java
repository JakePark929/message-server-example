package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.AcceptRequest;
import com.jake.messagesystem.dto.websocket.outbound.AcceptNotification;
import com.jake.messagesystem.dto.websocket.outbound.AcceptResponse;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class AcceptRequestHandler implements BaseRequestHandler<AcceptRequest> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public AcceptRequestHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, AcceptRequest request) {
        final UserId acceptorUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final Pair<Optional<UserId>, String> accept = userConnectionService.accept(acceptorUserId, request.getUsername());
        accept.getFirst().ifPresentOrElse(inviterUserId -> {
            clientNotificationService.sendMessage(senderSession, acceptorUserId, new AcceptResponse(request.getUsername()));
            String acceptUsername = accept.getSecond();
            clientNotificationService.sendMessage(inviterUserId, new AcceptNotification(acceptUsername));
        }, () -> {
            String errorMessage = accept.getSecond();
            clientNotificationService.sendMessage(senderSession, acceptorUserId, new ErrorResponse(MessageType.ACCEPT_REQUEST, errorMessage));
        });
    }
}
