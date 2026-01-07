package com.jake.messagesystem.dto.kafka.inbound;

import com.jake.messagesystem.constant.MessageType;
import com.jake.messagesystem.dto.UserId;

public record AcceptNotificationRecord(UserId userId, String username) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.NOTIFY_ACCEPT;
    }
}
