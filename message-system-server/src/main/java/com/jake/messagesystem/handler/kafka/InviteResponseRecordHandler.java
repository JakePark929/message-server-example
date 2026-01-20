package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.InviteResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.InviteResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class InviteResponseRecordHandler implements BaseRecordHandler<InviteResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public InviteResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(InviteResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new InviteResponse(record.inviteCode(), record.status()), record);
    }
}
