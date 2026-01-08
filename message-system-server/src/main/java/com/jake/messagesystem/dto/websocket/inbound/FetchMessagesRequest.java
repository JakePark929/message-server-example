package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;

public class FetchMessagesRequest extends BaseRequest {
    private final ChannelId channelId;
    private final MessageSeqId startMessageSeqId;
    private final MessageSeqId endMessageSeqId;

    @JsonCreator
    public FetchMessagesRequest(
            @JsonProperty("channelId") ChannelId channelId,
            @JsonProperty("startMessageSeqId") MessageSeqId startMessageSeqId,
            @JsonProperty("endMessageSeqId") MessageSeqId endMessageSeqId
    ) {
        super(MessageType.FETCH_MESSAGES_REQUEST);
        this.channelId = channelId;
        this.startMessageSeqId = startMessageSeqId;
        this.endMessageSeqId = endMessageSeqId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public MessageSeqId getStartMessageSeqId() {
        return startMessageSeqId;
    }

    public MessageSeqId getEndMessageSeqId() {
        return endMessageSeqId;
    }
}
