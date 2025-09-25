package com.jake.messagesystem.handler;

import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.util.JsonUtil;
import jakarta.websocket.MessageHandler;

public class WebSocketMessageHandler implements MessageHandler.Whole<String> {
    private final TerminalService terminalService;

    public WebSocketMessageHandler(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @Override
    public void onMessage(String payload) {
        JsonUtil.fromJson(payload, Message.class)
                .ifPresent(message -> terminalService.printMessage(message.username(), message.content()));
    }
}
