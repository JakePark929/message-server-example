package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class PushService {
    private static final Logger log = LoggerFactory.getLogger(PushService.class);

    private final HashSet<String> pushMessageTypes = new HashSet<>();

    public void registerPushMessageType(String pushMessageType) {
        pushMessageTypes.add(pushMessageType);
    }

    public void pushMessage(UserId userId, String messageType, String message) {
        if (pushMessageTypes.contains(messageType)) {
            log.info("Push message: {} to user: {}", message, userId);
        }
    }
}
