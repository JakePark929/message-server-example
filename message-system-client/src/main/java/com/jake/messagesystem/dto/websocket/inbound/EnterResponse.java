package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;

public class EnterResponse extends BaseMessage {
    private final ChannelId channelId;
    private final String title;
    private final MessageSeqId lastReadMessageSeqId;
    private final MessageSeqId lastChannelMessageSeqId;

    @JsonCreator
    public EnterResponse(
            @JsonProperty("channelId") ChannelId channelId,
            @JsonProperty("title") String title,
            @JsonProperty("lastReadMessageSeqId") MessageSeqId lastReadMessageSeqId,
            @JsonProperty("lastChannelMessageSeqId") MessageSeqId lastChannelMessageSeqId
    ) {
        super(MessageType.ENTER_RESPONSE);
        this.channelId = channelId;
        this.title = title;
        this.lastReadMessageSeqId = lastReadMessageSeqId;
        this.lastChannelMessageSeqId = lastChannelMessageSeqId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public String getTitle() {
        return title;
    }

    public MessageSeqId getLastReadMessageSeqId() {
        return lastReadMessageSeqId;
    }

    public MessageSeqId getLastChannelMessageSeqId() {
        return lastChannelMessageSeqId;
    }
}
