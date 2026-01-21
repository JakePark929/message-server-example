package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;

import java.util.List;

public record CreateRequestRecord(
        UserId userId,
        String title,
        List<String> participantUsernames
) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.CREATE_REQUEST;
    }
}
