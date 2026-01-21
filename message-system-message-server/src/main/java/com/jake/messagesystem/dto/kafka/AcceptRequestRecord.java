package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;

public record AcceptRequestRecord(UserId userId, String username) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.ACCEPT_REQUEST;
    }
}
