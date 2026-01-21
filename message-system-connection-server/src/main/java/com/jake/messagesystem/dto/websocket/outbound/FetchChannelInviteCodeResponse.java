package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.InviteCode;

public class FetchChannelInviteCodeResponse extends BaseMessage {
    private final ChannelId channelId;
    private final InviteCode inviteCode;

    public FetchChannelInviteCodeResponse(ChannelId channelId, InviteCode inviteCode) {
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
