package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.DisconnectRequestRecord;
import com.jake.messagesystem.dto.websocket.inbound.DisconnectRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.kafka.KafkaProducer;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class DisconnectRequestHandler implements BaseRequestHandler<DisconnectRequest> {
    private final KafkaProducer kafkaProducer;
    private final ClientNotificationService clientNotificationService;

    public DisconnectRequestHandler(KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
        this.kafkaProducer = kafkaProducer;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, DisconnectRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        kafkaProducer.sendRequest(new DisconnectRequestRecord(senderUserId, request.getUsername()), () ->
                clientNotificationService.sendErrorMessage(senderSession, new ErrorResponse(MessageType.DISCONNECT_REQUEST, "Disconnect failed."))
        );
    }
}
