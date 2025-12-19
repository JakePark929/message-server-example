package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.InviteCode;

public class InviteResponse extends BaseMessage {
    private final InviteCode inviteCode;
    private final UserConnectionStatus status;

    @JsonCreator
    public InviteResponse(@JsonProperty("inviteCode") InviteCode inviteCode,@JsonProperty("status") UserConnectionStatus status) {
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
