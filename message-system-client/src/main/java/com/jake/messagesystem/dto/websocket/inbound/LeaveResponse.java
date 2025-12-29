package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jake.messagesystem.constants.MessageType;

public class LeaveResponse extends BaseMessage {

    @JsonCreator
    public LeaveResponse() {
        super(MessageType.LEAVE_RESPONSE);
    }
}
