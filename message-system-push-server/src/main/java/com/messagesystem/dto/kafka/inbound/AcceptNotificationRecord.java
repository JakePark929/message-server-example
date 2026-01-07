package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.dto.UserId;

public record AcceptNotificationRecord(UserId userId, String username) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.NOTIFY_ACCEPT;
    }
}
