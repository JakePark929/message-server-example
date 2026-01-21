package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;

public record JoinResponseRecord(UserId userId, ChannelId channelId, String title) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.JOIN_RESPONSE;
    }
}
