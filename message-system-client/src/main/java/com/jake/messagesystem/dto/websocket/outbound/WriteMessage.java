package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class WriteMessage extends BaseRequest {
    private final Long serial;
    private final ChannelId channelId;
    private final String content;

    public WriteMessage(Long serial, ChannelId channelId, String content) {
        super(MessageType.WRITE_MESSAGE);
        this.serial = serial;
        this.channelId = channelId;
        this.content = content;
    }

    public Long getSerial() {
        return serial;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }
}
