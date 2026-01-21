package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.Connection;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.FetchConnectionsRequestRecord;
import com.jake.messagesystem.dto.kafka.FetchConnectionsResponseRecord;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FetchConnectionsRequestRecordHandler implements BaseRecordHandler<FetchConnectionsRequestRecord> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public FetchConnectionsRequestRecordHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchConnectionsRequestRecord record) {
        final UserId senderUserId = record.userId();

        final List<Connection> connections = userConnectionService.getUsersByStatus(senderUserId, record.status()).stream().map(user -> new Connection(user.username(), record.status())).toList();
        clientNotificationService.sendMessage(senderUserId, new FetchConnectionsResponseRecord(senderUserId, connections));
    }
}
