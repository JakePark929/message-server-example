package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.dto.UserId;

public record InviteNotificationRecord(UserId userId, String username) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.ASK_INVITE;
    }
}
