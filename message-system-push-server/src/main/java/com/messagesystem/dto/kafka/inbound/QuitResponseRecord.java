package com.messagesystem.dto.kafka.inbound;

import com.messagesystem.constant.MessageType;
import com.messagesystem.dto.ChannelId;
import com.messagesystem.dto.UserId;

public record QuitResponseRecord(UserId userId, ChannelId channelId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.QUIT_RESPONSE;
    }
}
