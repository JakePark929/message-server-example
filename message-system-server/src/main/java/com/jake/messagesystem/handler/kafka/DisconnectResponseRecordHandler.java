package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.DisconnectResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.DisconnectResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class DisconnectResponseRecordHandler implements BaseRecordHandler<DisconnectResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public DisconnectResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(DisconnectResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new DisconnectResponse(record.username(), record.status()), record);
    }
}
