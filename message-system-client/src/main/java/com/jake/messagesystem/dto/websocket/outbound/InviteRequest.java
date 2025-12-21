package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.InviteCode;

public class InviteRequest extends BaseRequest {
    private final InviteCode userInviteCode;

    public InviteRequest(InviteCode userInviteCode) {
        super(MessageType.INVITE_REQUEST);
        this.userInviteCode = userInviteCode;
    }

    public InviteCode getUserInviteCode() {
        return userInviteCode;
    }
}
