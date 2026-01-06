package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.constant.UserConnectionStatus;
import com.messagesystem.dto.InviteCode;
import com.messagesystem.dto.UserId;

public record InviteResponseRecord(UserId userId, InviteCode inviteCode, UserConnectionStatus status) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.INVITE_RESPONSE;
    }
}
