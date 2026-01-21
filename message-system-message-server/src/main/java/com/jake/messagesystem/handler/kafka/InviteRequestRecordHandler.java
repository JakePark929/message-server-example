package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.InviteNotificationRecord;
import com.jake.messagesystem.dto.kafka.InviteRequestRecord;
import com.jake.messagesystem.dto.kafka.InviteResponseRecord;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserConnectionService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InviteRequestRecordHandler implements BaseRecordHandler<InviteRequestRecord> {
    private final UserConnectionService userConnectionService;
    private final ClientNotificationService clientNotificationService;

    public InviteRequestRecordHandler(UserConnectionService userConnectionService, ClientNotificationService clientNotificationService) {
        this.userConnectionService = userConnectionService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(InviteRequestRecord record) {
        final UserId inviterUserId = record.userId();

        final Pair<Optional<UserId>, String> invite = userConnectionService.invite(inviterUserId, record.userInviteCode());

        invite.getFirst().ifPresentOrElse(partnerUserId -> {
            final String inviterUsername = invite.getSecond();

            clientNotificationService.sendMessage(inviterUserId, new InviteResponseRecord(inviterUserId, record.userInviteCode(), UserConnectionStatus.PENDING));
            clientNotificationService.sendMessage(partnerUserId, new InviteNotificationRecord(partnerUserId, inviterUsername));
        }, () -> {
            String errorMessage = invite.getSecond();
            clientNotificationService.sendError(new ErrorResponseRecord(inviterUserId, MessageType.INVITE_REQUEST, errorMessage));
        });
    }
}
