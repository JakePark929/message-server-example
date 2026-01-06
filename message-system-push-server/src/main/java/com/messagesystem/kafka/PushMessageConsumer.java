package com.messagesystem.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PushMessageConsumer {
    private static final Logger log = LoggerFactory.getLogger(PushMessageConsumer.class);

    @KafkaListener(
            topics = "${message-system.kafka.listeners.push.topic}",
            groupId = "${message-system.kafka.listeners.push.group-id}",
            concurrency = "${message-system.kafka.listeners.push.concurrency}"
    )
    public void consumeMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info(
                "Received record: {}, from topic: {}, on key: {}, partition: {}, offset: {}",
                consumerRecord.value(),
                consumerRecord.topic(),
                consumerRecord.key(),
                consumerRecord.partition(),
                consumerRecord.offset()
        );

        acknowledgment.acknowledge();
    }
}
