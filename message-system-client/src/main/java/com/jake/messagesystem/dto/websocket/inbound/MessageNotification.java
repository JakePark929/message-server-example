package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class MessageNotification extends BaseMessage {
    private final ChannelId channelId;
    private final String username;
    private final String content;

    @JsonCreator
    public MessageNotification(@JsonProperty("channelId") ChannelId channelId, @JsonProperty("username") String username, @JsonProperty("content") String content) {
        super(MessageType.NOTIFY_MESSAGE);
        this.channelId = channelId;
        this.username = username;
        this.content = content;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
