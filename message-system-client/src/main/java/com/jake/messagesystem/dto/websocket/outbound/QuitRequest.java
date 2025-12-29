package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class QuitRequest extends BaseRequest {
    private final ChannelId channelId;

    public QuitRequest(ChannelId channelId) {
        super(MessageType.QUIT_REQUEST);
        this.channelId = channelId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }
}
