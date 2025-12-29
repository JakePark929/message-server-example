package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.InviteCode;

public class FetchChannelInviteCodeResponse extends BaseMessage {
    private final ChannelId channelId;
    private final InviteCode inviteCode;

    @JsonCreator
    public FetchChannelInviteCodeResponse(@JsonProperty("channelId") ChannelId channelId, @JsonProperty("inviteCode") InviteCode inviteCode) {
        super(MessageType.FETCH_CHANNEL_INVITE_CODE_RESPONSE);
        this.channelId = channelId;
        this.inviteCode = inviteCode;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public InviteCode getInviteCode() {
        return inviteCode;
    }
}
