package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class FetchChannelsRequest extends BaseRequest {

    public FetchChannelsRequest() {
        super(MessageType.FETCH_CHANNELS_REQUEST);
    }
}
