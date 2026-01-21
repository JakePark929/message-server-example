package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.EnterResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.EnterResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class EnterResponseRecordHandler implements BaseRecordHandler<EnterResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public EnterResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(EnterResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new EnterResponse(record.channelId(), record.title(), record.lastReadMessageSeqId(), record.lastChannelMessageSeqId()), record);
    }
}
