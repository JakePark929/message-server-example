package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.Channel;
import com.jake.messagesystem.dto.UserId;

import java.util.List;

public record FetchChannelsResponseRecord(UserId userId, List<Channel> channels) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_CHANNELS_RESPONSE;
    }
}
