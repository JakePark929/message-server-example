package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.Connection;

import java.util.List;

public class FetchConnectionsResponse extends BaseMessage {
    private final List<Connection> connections;

    public FetchConnectionsResponse(List<Connection> connections) {
        super(MessageType.FETCH_CONNECTIONS_RESPONSE);
        this.connections = connections;
    }

    public List<Connection> getConnections() {
        return connections;
    }
}
