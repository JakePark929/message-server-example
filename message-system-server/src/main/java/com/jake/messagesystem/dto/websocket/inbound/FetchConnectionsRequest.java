package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;

public class FetchConnectionsRequest extends BaseRequest {
    private final UserConnectionStatus status;

    @JsonCreator
    public FetchConnectionsRequest(@JsonProperty("status") UserConnectionStatus status) {
        super(MessageType.FETCH_CONNECTIONS_REQUEST);
        this.status = status;
    }

    public UserConnectionStatus getStatus() { return status; }
}
