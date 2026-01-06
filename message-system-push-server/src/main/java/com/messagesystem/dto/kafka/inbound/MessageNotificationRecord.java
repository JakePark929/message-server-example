package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.dto.ChannelId;
import com.messagesystem.dto.UserId;

public record MessageNotificationRecord(UserId userId, ChannelId channelId, String username, String content) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.NOTIFY_MESSAGE;
    }
}
