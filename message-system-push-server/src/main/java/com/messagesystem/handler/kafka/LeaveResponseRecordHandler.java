package com.messagesystem.handler.kafka;

import com.messagesystem.dto.kafka.inbound.LeaveResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LeaveResponseRecordHandler implements BaseRecordHandler<LeaveResponseRecord> {
    private static final Logger log = LoggerFactory.getLogger(LeaveResponseRecordHandler.class);

    @Override
    public void handleRecord(LeaveResponseRecord record) {
        log.info("{} to offline userId: {}", record, record.userId());
    }
}
