package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.InviteCode;

public class JoinRequest extends BaseRequest {
    private final InviteCode inviteCode;

    public JoinRequest(InviteCode inviteCode) {
        super(MessageType.JOIN_REQUEST);
        this.inviteCode = inviteCode;
    }

    public InviteCode getInviteCode() {
        return inviteCode;
    }
}
