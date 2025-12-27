package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.InviteRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.InviteNotification;
import com.jake.messagesystem.dto.websocket.outbound.InviteResponse;
import com.jake.messagesystem.service.UserConnectionService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class InviteRequestHandler implements BaseRequestHandler<InviteRequest> {
    private final UserConnectionService userConnectionService;
    private final WebSocketSessionManager webSocketSessionManager;

    public InviteRequestHandler(UserConnectionService userConnectionService, WebSocketSessionManager webSocketSessionManager) {
        this.userConnectionService = userConnectionService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, InviteRequest request) {
        final UserId inviterUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final Pair<Optional<UserId>, String> invite = userConnectionService.invite(inviterUserId, request.getUserInviteCode());

        invite.getFirst().ifPresentOrElse(partnerUserId -> {
            final String inviterUsername = invite.getSecond();

            webSocketSessionManager.sendMessage(senderSession, new InviteResponse(request.getUserInviteCode(), UserConnectionStatus.PENDING));
            webSocketSessionManager.sendMessage(webSocketSessionManager.getSession(partnerUserId), new InviteNotification(inviterUsername));
        }, () -> {
            String errorMessage = invite.getSecond();
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.INVITE_REQUEST, errorMessage));
        });
    }
}
