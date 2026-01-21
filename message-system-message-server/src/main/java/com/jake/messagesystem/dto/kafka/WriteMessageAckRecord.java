package com.jake.messagesystem.dto.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;

public record WriteMessageAckRecord(UserId userId, Long serial, MessageSeqId messageSeqId) implements RecordInterface {
    @Override
    public String type() {
        return MessageType.WRITE_MESSAGE_ACK;
    }
}
