package com.jake.messagesystem.dto.kafka.inbound;

import com.jake.messagesystem.constant.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;

import java.util.List;

public record MessageNotificationRecord(
        UserId userId,
        ChannelId channelId,
        MessageSeqId messageSeqId,
        String username,
        String content,
        List<UserId> participantIds
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.NOTIFY_MESSAGE;
    }
}
