package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.LeaveResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.LeaveResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class LeaveResponseRecordHandler implements BaseRecordHandler<LeaveResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public LeaveResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(LeaveResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new LeaveResponse(), record);
    }
}
