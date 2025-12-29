package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.QuitRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.QuitResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class QuitRequestHandler implements BaseRequestHandler<QuitRequest> {
    private final ChannelService channelService;
    private final WebSocketSessionManager webSocketSessionManager;

    public QuitRequestHandler(ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
        this.channelService = channelService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, QuitRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        final ResultType quit;
        try {
            quit = channelService.quit(request.getChannelId(), senderUserId);
        } catch (Exception e) {
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.QUIT_REQUEST, ResultType.FAILED.getMessage()));

            return;
        }

        if (quit == ResultType.SUCCESS) {
            webSocketSessionManager.sendMessage(senderSession, new QuitResponse(request.getChannelId()));
        } else {
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.QUIT_REQUEST, quit.getMessage()));
        }
    }
}
