package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;

public class MessageNotification extends BaseMessage {
    private final String username;
    private final String content;

    @JsonCreator
    public MessageNotification(@JsonProperty("username") String username,@JsonProperty("content") String content) {
        super(MessageType.NOTIFY_MESSAGE);
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
