package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.dto.UserId;

public record LeaveResponseRecord(UserId userId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.LEAVE_RESPONSE;
    }
}
