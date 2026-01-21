package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.AcceptNotificationRecord;
import com.jake.messagesystem.dto.kafka.AcceptRequestRecord;
import com.jake.messagesystem.dto.kafka.AcceptResponseRecord;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AcceptRequestRecordHandler implements BaseRecordHandler<AcceptRequestRecord> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public AcceptRequestRecordHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(AcceptRequestRecord record) {
        final UserId acceptorUserId = record.userId();

        final Pair<Optional<UserId>, String> accept = userConnectionService.accept(acceptorUserId, record.username());
        accept.getFirst().ifPresentOrElse(inviterUserId -> {
            clientNotificationService.sendMessage(acceptorUserId, new AcceptResponseRecord(acceptorUserId, record.username()));
            String acceptUsername = accept.getSecond();
            clientNotificationService.sendMessage(inviterUserId, new AcceptNotificationRecord(inviterUserId, acceptUsername));
        }, () -> {
            String errorMessage = accept.getSecond();
            clientNotificationService.sendError(new ErrorResponseRecord(acceptorUserId, MessageType.ACCEPT_REQUEST, errorMessage));
        });
    }
}
