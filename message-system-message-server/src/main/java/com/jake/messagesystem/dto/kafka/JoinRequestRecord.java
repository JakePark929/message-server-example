package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.UserId;

public record JoinRequestRecord(UserId userId, InviteCode inviteCode) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.JOIN_REQUEST;
    }
}
