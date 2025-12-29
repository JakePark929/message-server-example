package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class JoinResponse extends BaseMessage {
    private final ChannelId channelId;
    private final String title;

    @JsonCreator
    public JoinResponse(@JsonProperty("channelId") ChannelId channelId, @JsonProperty("title") String title) {
        super(MessageType.JOIN_RESPONSE);
        this.channelId = channelId;
        this.title = title;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public String getTitle() {
        return title;
    }
}
