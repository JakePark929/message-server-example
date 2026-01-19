package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.InviteCode;

public record FetchUserInviteCodeResponseRecord(ChannelId channelId, InviteCode inviteCode) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.FETCH_USER_INVITE_CODE_RESPONSE;
    }
}
