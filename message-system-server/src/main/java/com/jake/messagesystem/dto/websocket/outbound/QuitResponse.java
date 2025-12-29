package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class QuitResponse extends BaseMessage {
    private final ChannelId channelId;

    public QuitResponse(ChannelId channelId) {
        super(MessageType.QUIT_RESPONSE);
        this.channelId = channelId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }
}
