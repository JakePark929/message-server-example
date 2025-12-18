package com.jake.messagesystem.dto.websocket.inbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.InviteCode;

public class FetchUserInviteCodeResponse extends BaseMessage {
    private final InviteCode inviteCode;

    public FetchUserInviteCodeResponse(InviteCode inviteCode) {
        super(MessageType.FETCH_USER_INVITE_CODE_RESPONSE);
        this.inviteCode = inviteCode;
    }

    public InviteCode getInviteCode() {
        return inviteCode;
    }
}
