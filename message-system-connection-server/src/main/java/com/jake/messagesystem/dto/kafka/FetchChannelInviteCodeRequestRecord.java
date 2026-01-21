package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;

public record FetchChannelInviteCodeRequestRecord(UserId userId, ChannelId channelId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_CHANNEL_INVITE_CODE_REQUEST;
    }
}
