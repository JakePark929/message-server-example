package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class InviteNotification extends BaseMessage {
    private final String username;

    public InviteNotification(String type, String username) {
        super(MessageType.ASK_INVITE);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
