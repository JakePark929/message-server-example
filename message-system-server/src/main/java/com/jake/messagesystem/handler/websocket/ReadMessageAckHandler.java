package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ReadMessageAckRecord;
import com.jake.messagesystem.dto.websocket.inbound.ReadMessageAck;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.kafka.KafkaProducer;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ReadMessageAckHandler implements BaseRequestHandler<ReadMessageAck> {
    private final KafkaProducer kafkaProducer;
    private final ClientNotificationService clientNotificationService;

    public ReadMessageAckHandler(KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
        this.kafkaProducer = kafkaProducer;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, ReadMessageAck request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
        ChannelId channelId = request.getChannelId();

        kafkaProducer.sendMessageUsingPartitionKey(
                channelId,
                senderUserId,
                new ReadMessageAckRecord(senderUserId, channelId, request.getMessageSeqId()),
                () -> clientNotificationService.sendErrorMessage(
                        senderSession,
                        new ErrorResponse(MessageType.READ_MESSAGE_ACK, "Read message ack failed.")
                )
        );
    }
}
