package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class KeepAliveRequest extends BaseRequest {

    public KeepAliveRequest() {
        super(MessageType.KEEP_ALIVE);
    }
}
