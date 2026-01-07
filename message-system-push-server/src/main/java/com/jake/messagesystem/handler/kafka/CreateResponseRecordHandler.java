package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.inbound.CreateResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateResponseRecordHandler implements BaseRecordHandler<CreateResponseRecord> {
    private static final Logger log = LoggerFactory.getLogger(CreateResponseRecordHandler.class);

    @Override
    public void handleRecord(CreateResponseRecord record) {
        log.info("{} to offline userId: {}", record, record.userId());
    }
}
