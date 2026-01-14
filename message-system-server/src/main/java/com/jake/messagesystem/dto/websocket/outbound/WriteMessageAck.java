package com.jake.messagesystem.dto.websocket.outbound;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.MessageSeqId;

public class WriteMessageAck extends BaseMessage {
    private final Long serial;
    private final MessageSeqId messageSeqId;

    public WriteMessageAck(Long serial, MessageSeqId messageSeqId) {
        super(MessageType.WRITE_MESSAGE_ACK);
        this.serial = serial;
        this.messageSeqId = messageSeqId;
    }

    public Long getSerial() {
        return serial;
    }

    public MessageSeqId getMessageSeqId() {
        return messageSeqId;
    }
}
