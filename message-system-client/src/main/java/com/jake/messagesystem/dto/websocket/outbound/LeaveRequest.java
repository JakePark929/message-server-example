package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class LeaveRequest extends BaseRequest {

    public LeaveRequest() {
        super(MessageType.LEAVE_REQUEST);
    }
}
