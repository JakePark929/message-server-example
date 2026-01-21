package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.FetchMessagesResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.FetchMessagesResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class FetchMessagesResponseRecordHandler implements BaseRecordHandler<FetchMessagesResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public FetchMessagesResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchMessagesResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new FetchMessagesResponse(record.channelId(), record.messages()), record);
    }
}
