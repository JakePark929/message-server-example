package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;

public record EnterRequestRecord(UserId userId, ChannelId channelId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.ENTER_REQUEST;
    }
}
