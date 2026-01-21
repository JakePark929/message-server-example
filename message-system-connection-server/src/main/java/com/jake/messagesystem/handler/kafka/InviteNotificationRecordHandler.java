package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.InviteNotificationRecord;
import com.jake.messagesystem.dto.websocket.outbound.InviteNotification;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class InviteNotificationRecordHandler implements BaseRecordHandler<InviteNotificationRecord> {
    private final ClientNotificationService clientNotificationService;

    public InviteNotificationRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(InviteNotificationRecord record) {
        clientNotificationService.sendMessage(record.userId(), new InviteNotification(record.username()), record);
    }
}
