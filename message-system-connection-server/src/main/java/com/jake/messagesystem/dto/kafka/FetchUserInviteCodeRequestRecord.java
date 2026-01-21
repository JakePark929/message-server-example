package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;

public record FetchUserInviteCodeRequestRecord(UserId userId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_USER_INVITE_CODE_REQUEST;
    }
}
