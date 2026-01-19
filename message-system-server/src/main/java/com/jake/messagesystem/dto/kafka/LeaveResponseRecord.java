package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;

public record LeaveResponseRecord(UserId userId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.LEAVE_RESPONSE;
    }
}
