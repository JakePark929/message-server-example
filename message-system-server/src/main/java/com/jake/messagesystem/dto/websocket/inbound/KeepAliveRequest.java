package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jake.messagesystem.constants.MessageType;

public class KeepAliveRequest extends BaseRequest {

    @JsonCreator
    public KeepAliveRequest() {
        super(MessageType.KEEP_ALIVE);
    }
}
