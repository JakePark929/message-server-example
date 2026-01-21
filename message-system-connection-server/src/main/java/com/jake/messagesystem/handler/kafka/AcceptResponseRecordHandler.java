package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.AcceptResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.AcceptResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class AcceptResponseRecordHandler implements BaseRecordHandler<AcceptResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public AcceptResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(AcceptResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new AcceptResponse(record.username()), record);
    }
}
