package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.WriteMessageRecord;
import com.jake.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;

@Component
public class WriteMessageRecordHandler implements BaseRecordHandler<WriteMessageRecord> {
    private final MessageService messageService;

    public WriteMessageRecordHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handleRecord(WriteMessageRecord record) {
        messageService.sendMessage(record);
    }
}
