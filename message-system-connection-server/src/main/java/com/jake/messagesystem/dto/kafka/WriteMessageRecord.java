package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;

public record WriteMessageRecord(
        UserId userId,
        Long serial,
        ChannelId channelId,
        String content,
        MessageSeqId messageSeqId
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.WRITE_MESSAGE;
    }
}
