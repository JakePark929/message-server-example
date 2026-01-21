package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.kafka.RecordInterface;

public interface BaseRecordHandler<T extends RecordInterface> {
    void handleRecord(T record);
}
