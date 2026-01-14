package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.ReadMessageAck;
import com.jake.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ReadMessageAckHandler implements BaseRequestHandler<ReadMessageAck> {
    private final MessageService messageService;

    public ReadMessageAckHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, ReadMessageAck request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
        messageService.updateLastReadMsgSeq(senderUserId, request.getChannelId(), request.getMessageSeqId());
    }
}
