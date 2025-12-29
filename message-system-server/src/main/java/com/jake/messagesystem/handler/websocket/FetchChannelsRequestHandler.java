package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.FetchChannelsRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchChannelsResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FetchChannelsRequestHandler implements BaseRequestHandler<FetchChannelsRequest> {
    private final ChannelService channelService;
    private final WebSocketSessionManager webSocketSessionManager;

    public FetchChannelsRequestHandler(ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
        this.channelService = channelService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchChannelsRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        webSocketSessionManager.sendMessage(senderSession, new FetchChannelsResponse(channelService.getChannels(senderUserId)));
    }
}
