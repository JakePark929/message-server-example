package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;

public record FetchMessagesRequestRecord(
        UserId userId,
        ChannelId channelId,
        MessageSeqId startMessageSeqId,
        MessageSeqId endMessageSeqId
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_MESSAGES_REQUEST;
    }
}
