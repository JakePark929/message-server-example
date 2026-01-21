package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;

public record ReadMessageAckRecord(
        UserId userId,
        ChannelId channelId,
        MessageSeqId messageSeqId
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.READ_MESSAGE_ACK;
    }
}
