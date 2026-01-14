package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.FetchMessagesRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.FetchMessagesResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.MessageService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class FetchMessagesRequestHandler implements BaseRequestHandler<FetchMessagesRequest> {
    private final MessageService messageService;
    private final ClientNotificationService clientNotificationService;

    public FetchMessagesRequestHandler(MessageService messageService, ClientNotificationService clientNotificationService) {
        this.messageService = messageService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchMessagesRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
        final ChannelId channelId = request.getChannelId();

        final Pair<List<Message>, ResultType> result = messageService.getMessages(channelId, request.getStartMessageSeqId(), request.getEndMessageSeqId());

        if (result.getSecond() == ResultType.SUCCESS) {
            final List<Message> messages = result.getFirst();
            clientNotificationService.sendMessage(senderSession, senderUserId, new FetchMessagesResponse(channelId, messages));
        } else {
            clientNotificationService.sendMessage(senderSession, senderUserId, new ErrorResponse(MessageType.FETCH_MESSAGES_REQUEST, result.getSecond().getMessage()));
        }
    }
}
