package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.FetchChannelInviteCodeRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.FetchChannelInviteCodeResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FetchChannelInviteCodeRequestHandler implements BaseRequestHandler<FetchChannelInviteCodeRequest> {
    private final ChannelService channelService;
    private final WebSocketSessionManager webSocketSessionManager;

    public FetchChannelInviteCodeRequestHandler(ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
        this.channelService = channelService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchChannelInviteCodeRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        if (!channelService.isJoined(request.getChannelId(), senderUserId)) {
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.FETCH_USER_INVITE_CODE_REQUEST, "Not joined the channel."));

            return;
        }

        channelService.getInviteCode(request.getChannelId()).ifPresentOrElse(
                inviteCode -> webSocketSessionManager.sendMessage(senderSession, new FetchChannelInviteCodeResponse(request.getChannelId(), inviteCode)),
                () -> webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.FETCH_CHANNEL_INVITE_CODE_REQUEST, "Fetch channel invite code failed."))
        );
    }
}
