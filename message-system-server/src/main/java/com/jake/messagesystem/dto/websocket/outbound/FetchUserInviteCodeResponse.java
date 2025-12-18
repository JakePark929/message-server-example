package com.jake.messagesystem.dto.websocket.outbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.InviteCode;

public class FetchUserInviteCodeResponse extends BaseMessage {
    private final InviteCode inviteCode;

    @JsonCreator
    public FetchUserInviteCodeResponse(@JsonProperty("userInviteCode") InviteCode inviteCode) {
        super(MessageType.FETCH_USER_INVITE_CODE_RESPONSE);
        this.inviteCode = inviteCode;
    }

    public InviteCode getInviteCode() {
        return inviteCode;
    }
}
