package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.RejectRequestRecord;
import com.jake.messagesystem.dto.kafka.RejectResponseRecord;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class RejectRequestRecordHandler implements BaseRecordHandler<RejectRequestRecord> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public RejectRequestRecordHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(RejectRequestRecord record) {
        final UserId senderUserId = record.userId();

        userConnectionService.reject(senderUserId, record.username());
        Pair<Boolean, String> reject = userConnectionService.reject(senderUserId, record.username());

        if (reject.getFirst()) {
            clientNotificationService.sendMessage(senderUserId, new RejectResponseRecord(senderUserId, record.username(), UserConnectionStatus.REJECTED));
        } else {
            String errorMessage = reject.getSecond();
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.REJECT_REQUEST, errorMessage));
        }
    }
}
