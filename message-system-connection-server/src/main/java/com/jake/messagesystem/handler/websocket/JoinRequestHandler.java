package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.JoinRequestRecord;
import com.jake.messagesystem.dto.websocket.inbound.JoinRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.kafka.KafkaProducer;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class JoinRequestHandler implements BaseRequestHandler<JoinRequest> {
    private final KafkaProducer kafkaProducer;
    private final ClientNotificationService clientNotificationService;

    public JoinRequestHandler(KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
        this.kafkaProducer = kafkaProducer;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, JoinRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        kafkaProducer.sendRequest(
                new JoinRequestRecord(senderUserId, request.getInviteCode()),
                () -> clientNotificationService.sendErrorMessage(senderSession, new ErrorResponse(MessageType.JOIN_REQUEST, "Join failed."))
        );
    }
}
