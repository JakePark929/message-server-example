package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jake.messagesystem.constants.MessageType;

public class KeepAlive extends BaseRequest {

    @JsonCreator
    public KeepAlive() {
        super(MessageType.KEEP_ALIVE);
    }
}
