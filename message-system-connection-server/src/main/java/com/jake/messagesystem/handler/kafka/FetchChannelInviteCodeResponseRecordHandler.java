package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.FetchChannelInviteCodeResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.FetchChannelInviteCodeResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class FetchChannelInviteCodeResponseRecordHandler implements BaseRecordHandler<FetchChannelInviteCodeResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public FetchChannelInviteCodeResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchChannelInviteCodeResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new FetchChannelInviteCodeResponse(record.channelId(), record.inviteCode()), record);
    }
}
