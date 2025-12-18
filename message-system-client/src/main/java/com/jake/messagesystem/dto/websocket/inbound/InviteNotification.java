package com.jake.messagesystem.dto.websocket.inbound;

import com.jake.messagesystem.constants.MessageType;

public class InviteNotification extends BaseMessage {
    private final String username;

    public InviteNotification(String username) {
        super(MessageType.ASK_INVITE);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
