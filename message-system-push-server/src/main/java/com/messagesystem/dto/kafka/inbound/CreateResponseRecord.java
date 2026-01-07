package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.dto.ChannelId;
import com.messagesystem.dto.UserId;

public record CreateResponseRecord(UserId userId, ChannelId channelId, String title) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.CREATE_RESPONSE;
    }
}
