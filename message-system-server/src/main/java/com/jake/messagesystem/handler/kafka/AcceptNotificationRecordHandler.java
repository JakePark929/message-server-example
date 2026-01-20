package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.AcceptNotificationRecord;
import com.jake.messagesystem.dto.websocket.outbound.AcceptNotification;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class AcceptNotificationRecordHandler implements BaseRecordHandler<AcceptNotificationRecord> {
    private final ClientNotificationService clientNotificationService;

    public AcceptNotificationRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(AcceptNotificationRecord record) {
        clientNotificationService.sendMessage(record.userId(), new AcceptNotification(record.username()), record);
    }
}
