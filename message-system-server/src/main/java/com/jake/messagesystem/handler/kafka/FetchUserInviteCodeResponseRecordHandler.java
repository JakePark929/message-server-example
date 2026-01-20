package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.FetchUserInviteCodeResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.FetchUserInviteCodeResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class FetchUserInviteCodeResponseRecordHandler implements BaseRecordHandler<FetchUserInviteCodeResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public FetchUserInviteCodeResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchUserInviteCodeResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new FetchUserInviteCodeResponse(record.inviteCode()), record);
    }
}
