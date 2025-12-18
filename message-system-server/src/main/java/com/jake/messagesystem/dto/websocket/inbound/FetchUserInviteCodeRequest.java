package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jake.messagesystem.constants.MessageType;

public class FetchUserInviteCodeRequest extends BaseRequest {

    @JsonCreator
    public FetchUserInviteCodeRequest() {
        super(MessageType.FETCH_USER_INVITE_CODE_REQUEST);
    }
}
