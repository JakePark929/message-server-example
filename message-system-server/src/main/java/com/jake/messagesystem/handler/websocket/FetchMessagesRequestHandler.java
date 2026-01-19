package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.FetchMessagesRequestRecord;
import com.jake.messagesystem.dto.websocket.inbound.FetchMessagesRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.kafka.KafkaProducer;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FetchMessagesRequestHandler implements BaseRequestHandler<FetchMessagesRequest> {
    private final KafkaProducer kafkaProducer;
    private final ClientNotificationService clientNotificationService;

    public FetchMessagesRequestHandler(KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
        this.kafkaProducer = kafkaProducer;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchMessagesRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
        final ChannelId channelId = request.getChannelId();

        kafkaProducer.sendMessageUsingPartitionKey(
                channelId,
                senderUserId,
                new FetchMessagesRequestRecord(senderUserId, channelId, request.getStartMessageSeqId(), request.getEndMessageSeqId()),
                () -> clientNotificationService.sendErrorMessage(senderSession, new ErrorResponse(MessageType.FETCH_MESSAGES_REQUEST, "Fetch messages failed."))
        );
    }
}
