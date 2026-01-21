package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.dto.websocket.inbound.BaseRequest;
import org.springframework.web.socket.WebSocketSession;

public interface BaseRequestHandler<T extends BaseRequest> {
    void handleRequest(WebSocketSession webSocketSession, T request);
}
