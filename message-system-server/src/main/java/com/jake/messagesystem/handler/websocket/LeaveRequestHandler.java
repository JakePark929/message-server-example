package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.LeaveRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.LeaveResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LeaveRequestHandler implements BaseRequestHandler<LeaveRequest> {
    private final ChannelService channelService;
    private final WebSocketSessionManager webSocketSessionManager;

    public LeaveRequestHandler(ChannelService channelService, WebSocketSessionManager webSocketSessionManager) {
        this.channelService = channelService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, LeaveRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        if (channelService.leave(senderUserId)) {
            webSocketSessionManager.sendMessage(senderSession, new LeaveResponse());
        } else {
            webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.LEAVE_REQUEST, "Leave failed."));
        }
    }
}
