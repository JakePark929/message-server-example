package com.jake.messagesystem.handler;

import com.jake.messagesystem.dto.websocket.inbound.MessageRequest;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.util.JsonUtil;
import jakarta.websocket.Session;

public class WebSocketSender {
    private final TerminalService terminalService;

    public WebSocketSender(final TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    public void sendMessage(Session session, MessageRequest message) {
        if (session != null && session.isOpen()) {
            JsonUtil.toJson(message).ifPresent(payload -> {
                session.getAsyncRemote().sendText(payload, result -> {
                    if (!result.isOK()) {
                        terminalService.printSystemMessage("'%s' send failed. cause: %s".formatted(payload, result.getException()));
                    }
                });
            });
        }
    }
}
