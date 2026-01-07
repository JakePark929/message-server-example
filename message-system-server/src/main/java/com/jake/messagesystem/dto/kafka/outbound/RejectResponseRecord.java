package com.jake.messagesystem.dto.kafka.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;

public record RejectResponseRecord(UserId userId, String username, UserConnectionStatus status) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.REJECT_RESPONSE;
    }
}
