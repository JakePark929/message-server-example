package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.Channel;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.CreateRequest;
import com.jake.messagesystem.dto.websocket.outbound.CreateResponse;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.JoinNotification;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class CreateRequestHandler implements BaseRequestHandler<CreateRequest> {
    private final ChannelService channelService;
    private final UserService userService;
    private final ClientNotificationService clientNotificationService;

    public CreateRequestHandler(ChannelService channelService, UserService userService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.userService = userService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, CreateRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final List<UserId> participantIds = userService.getUserIds(request.getParticipantUsernames());
        if (participantIds.isEmpty()) {
            clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.NOT_FOUND.getMessage()));

            return;
        }

        Pair<Optional<Channel>, ResultType> create;
        try {
            create = channelService.create(senderUserId, participantIds, request.getTitle());
        } catch (Exception e) {
            clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.FAILED.getMessage()));

            return;
        }

        if (create.getFirst().isEmpty()) {
            clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.CREATE_REQUEST, create.getSecond().getMessage()));

            return;
        }

        final Channel channel = create.getFirst().get();
        clientNotificationService.sendMessage(senderSession, senderUserId, new CreateResponse(channel.channelId(), channel.title()));
        participantIds.forEach(participantId ->
                CompletableFuture.runAsync(() ->
                        clientNotificationService.sendMessage(participantId, new JoinNotification(channel.channelId(), channel.title()))
                )
        );
    }
}
