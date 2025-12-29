package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;

public class FetchChannelInviteCodeRequest extends BaseRequest {
    private final ChannelId channelId;

    public FetchChannelInviteCodeRequest(ChannelId channelId) {
        super(MessageType.FETCH_CHANNEL_INVITE_CODE_REQUEST);
        this.channelId = channelId;
    }

    public ChannelId getChannelId() {
        return channelId;
    }
}
