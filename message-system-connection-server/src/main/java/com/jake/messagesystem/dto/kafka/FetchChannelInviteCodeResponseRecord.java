package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.UserId;

public record FetchChannelInviteCodeResponseRecord(
        UserId userId,
        ChannelId channelId,
        InviteCode inviteCode
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_CHANNEL_INVITE_CODE_RESPONSE;
    }
}
