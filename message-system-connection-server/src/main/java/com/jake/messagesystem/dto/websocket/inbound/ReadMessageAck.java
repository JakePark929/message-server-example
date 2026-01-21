package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;

public class ReadMessageAck extends BaseRequest {
    private final ChannelId channelId;
    private final MessageSeqId messageSeqId;

    @JsonCreator
    public ReadMessageAck(@JsonProperty("channelId") ChannelId channelId, @JsonProperty("messageSeqId") MessageSeqId messageSeqId) {
        super(MessageType.READ_MESSAGE_ACK);
        this.channelId = channelId;
        this.messageSeqId = messageSeqId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public MessageSeqId getMessageSeqId() {
        return messageSeqId;
    }
}
