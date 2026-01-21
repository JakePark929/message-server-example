package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.kafka.RecordInterface;
import com.jake.messagesystem.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PushService {
    private static final Logger log = LoggerFactory.getLogger(PushService.class);

    private final KafkaProducer kafkaProducer;

    private final HashMap<String, Class<? extends RecordInterface>> pushMessageTypes = new HashMap<>();

    public PushService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void registerPushMessageType(String pushMessageType, Class<? extends RecordInterface> clazz) {
        pushMessageTypes.put(pushMessageType, clazz);
    }

    public void pushMessage(RecordInterface recordInterface) {
        String messageType = recordInterface.type();
        if (pushMessageTypes.containsKey(messageType)) {
            kafkaProducer.sendPushNotification(recordInterface);
        } else {
            log.error("Invalid message type: {}", messageType);
        }
    }
}
