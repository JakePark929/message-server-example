package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.FetchChannelsResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.FetchChannelsResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class FetchChannelsResponseRecordHandler implements BaseRecordHandler<FetchChannelsResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public FetchChannelsResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchChannelsResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new FetchChannelsResponse(record.channels()), record);
    }
}
