package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.WriteMessageAckRecord;
import com.jake.messagesystem.dto.websocket.outbound.WriteMessageAck;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class WriteMessageAckRecordHandler implements BaseRecordHandler<WriteMessageAckRecord> {
    private final ClientNotificationService clientNotificationService;

    public WriteMessageAckRecordHandler(ClientNotificationService clientNotificationService) {
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(WriteMessageAckRecord record) {
        clientNotificationService.sendMessage(record.userId(), new WriteMessageAck(record.serial(), record.messageSeqId()), record);
    }
}
