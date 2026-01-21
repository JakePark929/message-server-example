package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;

public record CreateResponseRecord(UserId userId, ChannelId channelId, String title) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.CREATE_RESPONSE;
    }
}
