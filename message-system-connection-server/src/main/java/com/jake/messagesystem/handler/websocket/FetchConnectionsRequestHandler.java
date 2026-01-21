package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.IdKey;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.FetchConnectionsRequestRecord;
import com.jake.messagesystem.dto.websocket.inbound.FetchConnectionsRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.kafka.KafkaProducer;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FetchConnectionsRequestHandler implements BaseRequestHandler<FetchConnectionsRequest> {
    private final KafkaProducer kafkaProducer;
    private final ClientNotificationService clientNotificationService;

    public FetchConnectionsRequestHandler(KafkaProducer kafkaProducer, ClientNotificationService clientNotificationService) {
        this.kafkaProducer = kafkaProducer;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchConnectionsRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(IdKey.USER_ID.getValue());

        kafkaProducer.sendRequest(new FetchConnectionsRequestRecord(senderUserId, request.getStatus()), () ->
                clientNotificationService.sendErrorMessage(senderSession, new ErrorResponse(MessageType.FETCH_CONNECTIONS_RESPONSE, "Fetch connections failed."))
        );
    }
}
