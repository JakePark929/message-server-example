package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class FetchUserInviteCodeRequest extends BaseRequest {

    public FetchUserInviteCodeRequest() {
        super(MessageType.FETCH_USER_INVITE_CODE_REQUEST);
    }
}
