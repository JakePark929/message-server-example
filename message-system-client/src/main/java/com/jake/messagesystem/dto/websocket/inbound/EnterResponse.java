package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class EnterResponse extends BaseMessage {
    private final ChannelId channelId;
    private final String title;

    @JsonCreator
    public EnterResponse(@JsonProperty("channelId") ChannelId channelId, @JsonProperty("title") String title) {
        super(MessageType.ENTER_RESPONSE);
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
