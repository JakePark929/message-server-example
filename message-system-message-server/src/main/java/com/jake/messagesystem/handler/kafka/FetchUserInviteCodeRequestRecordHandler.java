package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.FetchUserInviteCodeRequestRecord;
import com.jake.messagesystem.dto.kafka.FetchUserInviteCodeResponseRecord;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class FetchUserInviteCodeRequestRecordHandler implements BaseRecordHandler<FetchUserInviteCodeRequestRecord> {
    private final UserService userService;
    private final ClientNotificationService clientNotificationService;

    public FetchUserInviteCodeRequestRecordHandler(UserService userService, ClientNotificationService clientNotificationService) {
        this.userService = userService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchUserInviteCodeRequestRecord record) {
        final UserId senderUserId = record.userId();
        userService.getInviteCode(senderUserId).ifPresentOrElse(
                inviteCode -> clientNotificationService.sendMessage(senderUserId, new FetchUserInviteCodeResponseRecord(senderUserId, inviteCode)),
                () -> clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.FETCH_USER_INVITE_CODE_REQUEST, "Fetch user invite code failed."))
        );
    }
}
