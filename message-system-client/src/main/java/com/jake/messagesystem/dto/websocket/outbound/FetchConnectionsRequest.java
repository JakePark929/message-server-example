package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;

public class FetchConnectionsRequest extends BaseRequest {
    private final UserConnectionStatus status;

    public FetchConnectionsRequest(UserConnectionStatus status) {
        super(MessageType.FETCH_CONNECTIONS_REQUEST);
        this.status = status;
    }

    public UserConnectionStatus getStatus() { return status; }
}
