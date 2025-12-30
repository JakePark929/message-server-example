package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.LeaveRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.LeaveResponse;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LeaveRequestHandler implements BaseRequestHandler<LeaveRequest> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public LeaveRequestHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, LeaveRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        if (channelService.leave(senderUserId)) {
            clientNotificationService.sendMessage(senderSession, senderUserId, new LeaveResponse());
        } else {
            clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.LEAVE_REQUEST, "Leave failed."));
        }
    }
}
