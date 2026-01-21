package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class EnterRequest extends BaseRequest {
    private final ChannelId channelId;

    @JsonCreator
    public EnterRequest(@JsonProperty("channelId") ChannelId channelId) {
        super(MessageType.ENTER_REQUEST);
        this.channelId = channelId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }
}
