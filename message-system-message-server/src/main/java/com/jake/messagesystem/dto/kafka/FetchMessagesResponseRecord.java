package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.dto.UserId;

import java.util.List;

public record FetchMessagesResponseRecord(UserId userId, ChannelId channelId, List<Message> messages) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_MESSAGES_RESPONSE;
    }
}
