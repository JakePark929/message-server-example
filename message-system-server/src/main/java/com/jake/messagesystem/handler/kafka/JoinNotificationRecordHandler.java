package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.JoinNotificationRecord;
import com.jake.messagesystem.dto.websocket.outbound.JoinNotification;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class JoinNotificationRecordHandler implements BaseRecordHandler<JoinNotificationRecord> {
    private final ClientNotificationService clientNotificationService;

    public JoinNotificationRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(JoinNotificationRecord record) {
        clientNotificationService.sendMessage(record.userId(), new JoinNotification(record.channelId(), record.title()), record);
    }
}
