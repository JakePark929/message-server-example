package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.Connection;
import com.jake.messagesystem.dto.UserId;

import java.util.List;

public record FetchConnectionsResponseRecord(UserId userId, List<Connection> connections) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_CONNECTIONS_RESPONSE;
    }
}
