package com.jake.messagesystem.dto.websocket.inbound;

public abstract class BaseRequest {
    private final String type;

    public BaseRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
