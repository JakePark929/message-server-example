package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class EnterResponse extends BaseMessage {
    private final ChannelId channelId;
    private final String title;

    public EnterResponse(ChannelId channelId, String title) {
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
