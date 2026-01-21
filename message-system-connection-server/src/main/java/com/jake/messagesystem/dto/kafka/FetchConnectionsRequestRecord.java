package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;

public record FetchConnectionsRequestRecord(UserId userId, UserConnectionStatus status) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_CONNECTIONS_REQUEST;
    }
}
