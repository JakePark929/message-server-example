package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.Message;

import java.util.List;

public class FetchMessagesResponse extends BaseMessage {
    private final ChannelId channelId;
    private final List<Message> messages;

    @JsonCreator
    public FetchMessagesResponse(@JsonProperty("channelId") ChannelId channelId, @JsonProperty("messages") List<Message> messages) {
        super(MessageType.FETCH_MESSAGES_RESPONSE);
        this.channelId = channelId;
        this.messages = messages;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
