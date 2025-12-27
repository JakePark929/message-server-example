package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;

public class CreateRequest extends BaseRequest {
    private final String title;
    private final String participantUsername;

    public CreateRequest(String title, String participantUsername) {
        super(MessageType.CREATE_REQUEST);
        this.title = title;
        this.participantUsername = participantUsername;
    }

    public String getParticipantUsername() {
        return participantUsername;
    }

    public String getTitle() {
        return title;
    }
}
