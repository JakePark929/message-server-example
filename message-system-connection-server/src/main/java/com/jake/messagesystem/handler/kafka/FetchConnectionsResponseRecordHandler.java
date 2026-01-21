package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.FetchConnectionsResponseRecord;
import com.jake.messagesystem.dto.websocket.outbound.FetchConnectionsResponse;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class FetchConnectionsResponseRecordHandler implements BaseRecordHandler<FetchConnectionsResponseRecord> {
    private final ClientNotificationService clientNotificationService;

    public FetchConnectionsResponseRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchConnectionsResponseRecord record) {
        clientNotificationService.sendMessage(record.userId(), new FetchConnectionsResponse(record.connections()), record);
    }
}
