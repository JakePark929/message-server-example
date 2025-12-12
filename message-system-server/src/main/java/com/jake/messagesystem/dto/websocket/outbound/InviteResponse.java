package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.InviteCode;

public class InviteResponse extends BaseMessage {
    private final InviteCode inviteCode;
    private final UserConnectionStatus status;

    public InviteResponse(String type, InviteCode inviteCode, UserConnectionStatus status) {
        super(MessageType.INVITE_RESPONSE);
        this.inviteCode = inviteCode;
        this.status = status;
    }

    public InviteCode getInviteCode() {
        return inviteCode;
    }

    public UserConnectionStatus getStatus() {
        return status;
    }
}
