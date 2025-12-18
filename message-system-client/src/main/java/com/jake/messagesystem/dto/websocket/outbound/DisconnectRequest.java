package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class DisconnectRequest extends BaseRequest {
    private final String username;

    public DisconnectRequest(String username) {
        super(MessageType.DISCONNECT_REQUEST);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
