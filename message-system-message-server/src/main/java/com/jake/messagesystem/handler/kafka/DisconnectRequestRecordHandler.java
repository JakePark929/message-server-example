package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.DisconnectRequestRecord;
import com.jake.messagesystem.dto.kafka.DisconnectResponseRecord;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class DisconnectRequestRecordHandler implements BaseRecordHandler<DisconnectRequestRecord> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public DisconnectRequestRecordHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(DisconnectRequestRecord record) {
        final UserId senderUserId = record.userId();
        final Pair<Boolean, String> disconnect = userConnectionService.disconnect(senderUserId, record.username());

        if (disconnect.getFirst()) {
            clientNotificationService.sendMessage(senderUserId, new DisconnectResponseRecord(senderUserId, record.username(), UserConnectionStatus.DISCONNECTED));
        } else {
            String errorMessage = disconnect.getSecond();
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.DISCONNECT_REQUEST, errorMessage));
        }
    }
}
