package com.jake.messagesystem.kafka;

import com.jake.messagesystem.dto.kafka.RecordInterface;
import com.jake.messagesystem.handler.kafka.RecordDispatcher;
import com.jake.messagesystem.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final JsonUtil jsonUtil;
    private final RecordDispatcher recordDispatcher;

    public KafkaConsumer(JsonUtil jsonUtil, RecordDispatcher recordDispatcher) {
        this.jsonUtil = jsonUtil;
        this.recordDispatcher = recordDispatcher;
    }

    @KafkaListener(
            topics = "${message-system.kafka.listeners.message.topic}",
            groupId = "${message-system.kafka.listeners.message.group-id}",
            concurrency = "${message-system.kafka.listeners.message.concurrency}"
    )
    public void messageTopicConsumerGroup(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        logInfo("messageTopicConsumerGroup", consumerRecord);

        jsonUtil.fromJson(consumerRecord.value(), RecordInterface.class).ifPresentOrElse(
                recordDispatcher::dispatchRecord,
                () -> logError("messageTopicConsumerGroup", consumerRecord)
        );

        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${message-system.kafka.listeners.request.topic}",
            groupId = "${message-system.kafka.listeners.request.group-id}",
            concurrency = "${message-system.kafka.listeners.request.concurrency}"
    )
    public void requestTopicConsumerGroup(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        logInfo("requestTopicConsumerGroup", consumerRecord);

        jsonUtil.fromJson(consumerRecord.value(), RecordInterface.class).ifPresentOrElse(
                recordDispatcher::dispatchRecord,
                () -> logError("requestTopicConsumerGroup", consumerRecord)
        );

        acknowledgment.acknowledge();
    }

    private void logInfo(String listener, ConsumerRecord<String, String> consumerRecord) {
        log.info(
                "Received listener: {}, record: {}, from topic: {}, on key: {}, partition: {}, offset: {}",
                listener,
                consumerRecord.value(),
                consumerRecord.topic(),
                consumerRecord.key(),
                consumerRecord.partition(),
                consumerRecord.offset()
        );
    }

    private void logError(String listener, ConsumerRecord<String, String> consumerRecord) {
        log.error(
                "Received listener: {}, record: {}, from topic: {}, on key: {}, partition: {}, offset: {}",
                listener,
                consumerRecord.value(),
                consumerRecord.topic(),
                consumerRecord.key(),
                consumerRecord.partition(),
                consumerRecord.offset()
        );
    }
}
