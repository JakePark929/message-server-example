package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.constant.UserConnectionStatus;
import com.messagesystem.dto.UserId;

public record DisconnectResponseRecord(UserId userId, String username, UserConnectionStatus status) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.DISCONNECT_RESPONSE;
    }
}
