package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ReadMessageAckRecord;
import com.jake.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;

@Component
public class ReadMessageAckRecordHandler implements BaseRecordHandler<ReadMessageAckRecord> {
    private final MessageService messageService;

    public ReadMessageAckRecordHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handleRecord(ReadMessageAckRecord record) {
        final UserId senderUserId = record.userId();
        messageService.updateLastReadMsgSeq(senderUserId, record.channelId(), record.messageSeqId());
    }
}
