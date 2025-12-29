package com.jake.messagesystem.dto.websocket.inbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jake.messagesystem.constants.MessageType;

public class FetchChannelsRequest extends BaseRequest {
    @JsonCreator
    public FetchChannelsRequest() {
        super(MessageType.FETCH_CHANNELS_REQUEST);
    }
}
