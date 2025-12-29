package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jake.messagesystem.constants.MessageType;

public class LeaveRequest extends BaseRequest {

    @JsonCreator
    public LeaveRequest() {
        super(MessageType.LEAVE_REQUEST);
    }
}
