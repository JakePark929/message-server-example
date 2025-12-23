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
import com.jake.messagesystem.service.UserService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class CreateRequestHandler implements BaseRequestHandler<CreateRequest> {
    private final ChannelService channelService;
    private final UserService userService;
    private final WebSocketSessionManager webSocketSessionManager;

    public CreateRequestHandler(ChannelService channelService, UserService userService, WebSocketSessionManager webSocketSessionManager) {
        this.channelService = channelService;
        this.userService = userService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, CreateRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final Optional<UserId> userId = userService.getUserId(request.getParticipantUsername());
        if (userId.isEmpty()) {
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.NOT_FOUND.getMessage()));

            return;
        }

        UserId participantId = userId.get();
        Pair<Optional<Channel>, ResultType> create;

        try {
            create = channelService.create(senderUserId, participantId, request.getTitle());
        } catch (Exception e) {
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.CREATE_REQUEST, ResultType.FAILED.getMessage()));

            return;
        }

        create.getFirst().ifPresentOrElse(
                channel -> {
                    webSocketSessionManager.sendMessage(senderSession, new CreateResponse(channel.channelId(), channel.title()));
                    webSocketSessionManager.sendMessage(webSocketSessionManager.getSession(participantId), new JoinNotification(channel.channelId(), channel.title()));
                }, () ->
                        webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.CREATE_REQUEST, create.getSecond().getMessage()))
        );
    }
}
