package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class MessageNotification extends BaseMessage {
    private final ChannelId channelId;
    private final String username;
    private final String content;

    public MessageNotification(ChannelId channelId, String username, String content) {
        super(MessageType.NOTIFY_MESSAGE);
        this.channelId = channelId;
        this.username = username;
        this.content = content;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
