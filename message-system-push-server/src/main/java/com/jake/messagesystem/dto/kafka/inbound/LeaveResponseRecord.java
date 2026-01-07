package com.jake.messagesystem.dto.kafka.inbound;

import com.jake.messagesystem.constant.MessageType;
import com.jake.messagesystem.dto.UserId;

public record LeaveResponseRecord(UserId userId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.LEAVE_RESPONSE;
    }
}
