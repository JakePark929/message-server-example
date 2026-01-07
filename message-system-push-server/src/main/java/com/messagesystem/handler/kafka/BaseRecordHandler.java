package com.messagesystem.handler.kafka;

import com.messagesystem.dto.kafka.inbound.RecordInterface;

public interface BaseRecordHandler<T extends RecordInterface> {
    void handleRecord(T record);
}
