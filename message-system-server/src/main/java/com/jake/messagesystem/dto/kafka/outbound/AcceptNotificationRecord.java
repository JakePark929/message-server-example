package com.jake.messagesystem.dto.kafka.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;

public record AcceptNotificationRecord(UserId userId, String username) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.NOTIFY_ACCEPT;
    }
}
