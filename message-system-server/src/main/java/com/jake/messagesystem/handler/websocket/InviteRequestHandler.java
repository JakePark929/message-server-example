package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.InviteRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.InviteNotification;
import com.jake.messagesystem.dto.websocket.outbound.InviteResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class InviteRequestHandler implements BaseRequestHandler<InviteRequest> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public InviteRequestHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, InviteRequest request) {
        final UserId inviterUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final Pair<Optional<UserId>, String> invite = userConnectionService.invite(inviterUserId, request.getUserInviteCode());

        invite.getFirst().ifPresentOrElse(partnerUserId -> {
            final String inviterUsername = invite.getSecond();

            clientNotificationService.sendMessage(senderSession, inviterUserId, new InviteResponse(request.getUserInviteCode(), UserConnectionStatus.PENDING));
            clientNotificationService.sendMessage(partnerUserId, new InviteNotification(inviterUsername));
        }, () -> {
            String errorMessage = invite.getSecond();
            clientNotificationService.sendMessage(senderSession, inviterUserId, new ErrorResponse(MessageType.INVITE_REQUEST, errorMessage));
        });
    }
}
