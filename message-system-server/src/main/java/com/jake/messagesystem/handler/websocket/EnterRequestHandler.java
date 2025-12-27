package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.EnterRequest;
import com.jake.messagesystem.dto.websocket.outbound.EnterResponse;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class EnterRequestHandler implements BaseRequestHandler<EnterRequest> {
    private final ChannelService channelService;
    private final WebSocketSessionManager webSocketSessionManager;

    public EnterRequestHandler(ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
        this.channelService = channelService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, EnterRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final Pair<Optional<String>, ResultType> enter = channelService.enter(request.getChannelId(), senderUserId);
        enter.getFirst().ifPresentOrElse(
                title -> webSocketSessionManager.sendMessage(senderSession, new EnterResponse(request.getChannelId(), title)),
                () -> webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.ENTER_REQUEST, enter.getSecond().getMessage())));
    }
}
