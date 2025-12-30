package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.RejectRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.RejectResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class RejectRequestHandler implements BaseRequestHandler<RejectRequest> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public RejectRequestHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, RejectRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        userConnectionService.reject(senderUserId, request.getUsername());
        Pair<Boolean, String> reject = userConnectionService.reject(senderUserId, request.getUsername());

        if (reject.getFirst()) {
            clientNotificationService.sendMessage(senderSession, senderUserId, new RejectResponse(request.getUsername(), UserConnectionStatus.REJECTED));
        } else {
            String errorMessage = reject.getSecond();
            clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.REJECT_REQUEST, errorMessage));
        }
    }
}
