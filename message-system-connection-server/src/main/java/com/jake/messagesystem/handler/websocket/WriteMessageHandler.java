package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.WriteMessageRecord;
import com.jake.messagesystem.dto.websocket.inbound.WriteMessage;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.kafka.KafkaProducer;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.MessageSeqIdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WriteMessageHandler implements BaseRequestHandler<WriteMessage> {
    private final KafkaProducer kafkaProducer;
    private final MessageSeqIdGenerator messageSeqIdGenerator;

    private final ClientNotificationService clientNotificationService;

    public WriteMessageHandler(KafkaProducer kafkaProducer, MessageSeqIdGenerator messageSeqIdGenerator, ClientNotificationService clientNotificationService) {
        this.kafkaProducer = kafkaProducer;
        this.messageSeqIdGenerator = messageSeqIdGenerator;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, WriteMessage request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());
        ChannelId channelId = request.getChannelId();

        Runnable errorCallback = () -> clientNotificationService.sendErrorMessage(
                senderSession,
                new ErrorResponse(MessageType.WRITE_MESSAGE, "Write message failed.")
        );

        messageSeqIdGenerator.getNext(channelId).ifPresentOrElse(messageSeqId ->
                kafkaProducer.sendMessageUsingPartitionKey(
                        channelId,
                        senderUserId,
                        new WriteMessageRecord(senderUserId, request.getSerial(), channelId, request.getContent(), messageSeqId),
                        errorCallback
                ),
                errorCallback
        );
    }
}
