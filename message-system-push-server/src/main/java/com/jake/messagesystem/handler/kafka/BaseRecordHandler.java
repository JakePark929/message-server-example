package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.inbound.RecordInterface;

public interface BaseRecordHandler<T extends RecordInterface> {
    void handleRecord(T record);
}
