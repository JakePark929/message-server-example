package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.CreateResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.CreateResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class CreateResponseRecordHandler implements BaseRecordHandler<CreateResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public CreateResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(CreateResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new CreateResponse(record.channelId(), record.title()), record);
    }
}
