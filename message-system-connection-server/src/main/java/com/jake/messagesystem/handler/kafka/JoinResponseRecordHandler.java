package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.JoinResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.JoinResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class JoinResponseRecordHandler implements BaseRecordHandler<JoinResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public JoinResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(JoinResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new JoinResponse(record.channelId(), record.title()), record);
    }
}
