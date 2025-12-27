package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class KeepAlive extends BaseRequest {

    public KeepAlive() {
        super(MessageType.KEEP_ALIVE);
    }
}
