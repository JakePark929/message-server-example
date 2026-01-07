package com.jake.messagesystem.dto.kafka.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;

public record MessageNotificationRecord(UserId userId, ChannelId channelId, String username, String content) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.NOTIFY_MESSAGE;
    }
}
