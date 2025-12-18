package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.Constants;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.RejectRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.RejectResponse;
import com.jake.messagesystem.service.UserConnectionService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class RejectRequestHandler implements BaseRequestHandler<RejectRequest> {
    private final UserConnectionService userConnectionService;
    private final WebSocketSessionManager webSocketSessionManager;

    public RejectRequestHandler(UserConnectionService userConnectionService, WebSocketSessionManager webSocketSessionManager) {
        this.userConnectionService = userConnectionService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, RejectRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(Constants.USER_ID.getValue());

        userConnectionService.reject(senderUserId, request.getUsername());
        Pair<Boolean, String> reject = userConnectionService.reject(senderUserId, request.getUsername());

        if (reject.getFirst()) {
            webSocketSessionManager.sendMessage(senderSession, new RejectResponse(request.getUsername(), UserConnectionStatus.REJECTED));
        } else {
            String errorMessage = reject.getSecond();
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.REJECT_REQUEST, errorMessage));
        }
    }
}
