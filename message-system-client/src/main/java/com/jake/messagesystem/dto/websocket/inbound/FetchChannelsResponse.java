package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.Channel;

import java.util.List;

public class FetchChannelsResponse extends BaseMessage {
    private final List<Channel> channels;

    @JsonCreator
    public FetchChannelsResponse(@JsonProperty("channels") List<Channel> channels) {
        super(MessageType.FETCH_CHANNELS_RESPONSE);
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }
}
