package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class QuitResponse extends BaseMessage {
    private final ChannelId channelId;

    @JsonCreator
    public QuitResponse(@JsonProperty("channelId") ChannelId channelId) {
        super(MessageType.QUIT_RESPONSE);
        this.channelId = channelId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }
}
