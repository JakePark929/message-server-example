package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.outbound.RecordInterface;
import com.jake.messagesystem.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PushService {
    private static final Logger log = LoggerFactory.getLogger(PushService.class);

    private final KafkaProducerService kafkaProducerService;

    private final JsonUtil jsonUtil;
    private final HashMap<String, Class<? extends RecordInterface>> pushMessageTypes = new HashMap<>();

    public PushService(KafkaProducerService kafkaProducerService, JsonUtil jsonUtil) {
        this.kafkaProducerService = kafkaProducerService;
        this.jsonUtil = jsonUtil;
    }

    public void registerPushMessageType(String pushMessageType, Class<? extends RecordInterface> clazz) {
        pushMessageTypes.put(pushMessageType, clazz);
    }

    public void pushMessage(UserId userId, String messageType, String message) {
        final Class<? extends RecordInterface> recordInterface = pushMessageTypes.get(messageType);
        if (recordInterface != null) {
            jsonUtil.addValue(message, "userId", userId.id().toString())
                    .flatMap(json -> jsonUtil.fromJson(json, recordInterface))
                    .ifPresent(kafkaProducerService::sendPushNotification);

            log.info("Push message: {} to user: {}", message, userId);
        } else {
            log.error("Invalid message type: {}", messageType);
        }

    }
}
