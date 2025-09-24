package com.jake.messagesystem.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionManager.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public List<WebSocketSession> getSessions() {
        return sessions.values().stream().toList();
    }

    public void storeSession(WebSocketSession webSocketSession) {
        log.info("Store Session : {}", webSocketSession.getId());
        sessions.put(webSocketSession.getId(), webSocketSession);
    }

    public void terminateSession(String sessionId) {
        try {
            WebSocketSession webSocketSession = sessions.remove(sessionId);
            if (webSocketSession != null) {
                log.info("Remove Session : {}", sessionId);
                webSocketSession.close();
                log.info("Close Session : {}", webSocketSession.getId());
            }
        } catch (IOException e) {
            log.error("Failed WebSocketSession close. sessionId: {}", sessionId);
        }
    }
}
