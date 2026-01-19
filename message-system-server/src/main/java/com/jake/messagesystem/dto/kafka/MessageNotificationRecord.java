package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;

import java.util.List;

public record MessageNotificationRecord(
        UserId userId,
        ChannelId channelId,
        String username,
        String content,
        List<UserId> participantIds
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.NOTIFY_MESSAGE;
    }
}
