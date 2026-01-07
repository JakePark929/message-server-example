package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.kafka.outbound.RecordInterface;
import com.jake.messagesystem.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonUtil jsonUtil;
    private final String pushTopic;

    public KafkaProducerService(
            KafkaTemplate<String, String> kafkaTemplate,
            JsonUtil jsonUtil,
            @Value("${message-system.kafka.listeners.push.topic}") String pushTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonUtil = jsonUtil;
        this.pushTopic = pushTopic;
    }

    public void sendPushNotification(RecordInterface recordInterface) {
        jsonUtil.toJson(recordInterface).ifPresent(record ->
                kafkaTemplate.send(pushTopic, record).whenComplete(((sendResult, throwable) -> {
                    if (throwable == null) {
                        log.info(
                                "Record produced: {} to topic: {}",
                                sendResult.getProducerRecord().value(),
                                sendResult.getProducerRecord().topic()
                        );
                    } else {
                        log.error(
                                "Record producing failed: {} to topic: {}, cause: {}",
                                record,
                                pushTopic,
                                throwable.getMessage()
                        );
                    }
                }))
        );
    }
}
