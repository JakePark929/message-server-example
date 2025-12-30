package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.FetchChannelsRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchChannelsResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FetchChannelsRequestHandler implements BaseRequestHandler<FetchChannelsRequest> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public FetchChannelsRequestHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchChannelsRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        clientNotificationService.sendMessage(senderSession, senderUserId, new FetchChannelsResponse(channelService.getChannels(senderUserId)));
    }
}
