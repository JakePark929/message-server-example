package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.Channel;

import java.util.List;

public class FetchChannelsResponse extends BaseMessage {
    private final List<Channel> channels;

    public FetchChannelsResponse(List<Channel> channels) {
        super(MessageType.FETCH_CHANNELS_RESPONSE);
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }
}
