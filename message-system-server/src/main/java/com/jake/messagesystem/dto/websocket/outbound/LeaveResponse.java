package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class LeaveResponse extends BaseMessage {

    public LeaveResponse() {
        super(MessageType.LEAVE_RESPONSE);
    }
}
