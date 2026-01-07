package com.jake.messagesystem.dto.kafka.inbound;

import com.jake.messagesystem.constant.MessageType;
import com.jake.messagesystem.constant.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;

public record RejectResponseRecord(UserId userId, String username, UserConnectionStatus status) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.REJECT_RESPONSE;
    }
}
