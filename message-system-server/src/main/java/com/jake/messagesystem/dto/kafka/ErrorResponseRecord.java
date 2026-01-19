package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;

public record ErrorResponseRecord(UserId userId, String messageType, String message) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.ERROR;
    }
}
