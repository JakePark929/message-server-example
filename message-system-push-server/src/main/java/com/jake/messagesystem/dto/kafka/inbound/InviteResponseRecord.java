package com.jake.messagesystem.dto.kafka.inbound;

import com.jake.messagesystem.constant.MessageType;
import com.jake.messagesystem.constant.UserConnectionStatus;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.UserId;

public record InviteResponseRecord(UserId userId, InviteCode inviteCode, UserConnectionStatus status) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.INVITE_RESPONSE;
    }
}
