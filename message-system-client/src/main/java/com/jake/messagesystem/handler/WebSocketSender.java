package com.jake.messagesystem.handler;

import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.util.JsonUtil;
import jakarta.websocket.Session;

public class WebSocketSender {
    private final TerminalService terminalService;

    public WebSocketSender(final TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    public void sendMessage(Session session, Message message) {
        if (session != null && session.isOpen()) {
            JsonUtil.toJson(message).ifPresent(msg -> {
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (Exception e) {
                    terminalService.printSystemMessage(String.format("%s send failed. error: %s", msg, e.getMessage()));
                }
            });
        }
    }
}
