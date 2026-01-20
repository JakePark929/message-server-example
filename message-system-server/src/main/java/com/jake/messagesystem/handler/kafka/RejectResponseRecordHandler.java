package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.RejectResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.RejectResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class RejectResponseRecordHandler implements BaseRecordHandler<RejectResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public RejectResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(RejectResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new RejectResponse(record.username(), record.status()), record);
    }
}
