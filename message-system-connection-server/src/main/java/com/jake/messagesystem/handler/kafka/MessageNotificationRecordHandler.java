package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.MessageNotificationRecord;
import com.jake.messagesystem.service.MessageService;
import org.springframework.stereotype.Component;

@Component
public class MessageNotificationRecordHandler implements BaseRecordHandler<MessageNotificationRecord> {
    private final MessageService messageService;

    public MessageNotificationRecordHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void handleRecord(MessageNotificationRecord record) {
        messageService.sendMessage(record);
    }
}
