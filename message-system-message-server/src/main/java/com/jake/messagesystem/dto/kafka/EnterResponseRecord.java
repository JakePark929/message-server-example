package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;

public record EnterResponseRecord(
        UserId userId,
        ChannelId channelId,
        String title,
        MessageSeqId lastReadMessageSeqId,
        MessageSeqId lastChannelMessageSeqId
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.ENTER_RESPONSE;
    }
}
