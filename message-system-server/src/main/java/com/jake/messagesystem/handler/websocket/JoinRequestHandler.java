package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.Channel;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.JoinRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.JoinResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class JoinRequestHandler implements BaseRequestHandler<JoinRequest> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public JoinRequestHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, JoinRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final Pair<Optional<Channel>, ResultType> join;
        try {
            join = channelService.join(request.getInviteCode(), senderUserId);
        } catch (Exception e) {
            clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.JOIN_REQUEST, ResultType.FAILED.getMessage()));

            return;
        }

        join.getFirst().ifPresentOrElse(
                channel -> clientNotificationService.sendMessage(senderSession, senderUserId, new JoinResponse(channel.channelId(), channel.title())),
                () -> clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.JOIN_REQUEST, join.getSecond().getMessage()))
        );
    }
}
