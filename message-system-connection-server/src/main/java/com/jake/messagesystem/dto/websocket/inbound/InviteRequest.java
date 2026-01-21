package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.InviteCode;

public class InviteRequest extends BaseRequest {
    private final InviteCode userInviteCode;

    @JsonCreator
    public InviteRequest(@JsonProperty("userInviteCode") InviteCode userInviteCode) {
        super(MessageType.INVITE_REQUEST);
        this.userInviteCode = userInviteCode;
    }

    public InviteCode getUserInviteCode() {
        return userInviteCode;
    }
}
