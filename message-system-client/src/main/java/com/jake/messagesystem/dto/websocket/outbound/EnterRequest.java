package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class EnterRequest extends BaseRequest {
    private final ChannelId channelId;

    public EnterRequest(ChannelId channelId) {
        super(MessageType.ENTER_REQUEST);
        this.channelId = channelId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }
}
