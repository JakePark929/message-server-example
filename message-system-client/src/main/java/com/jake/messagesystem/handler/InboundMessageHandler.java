package com.jake.messagesystem.handler;

import com.jake.messagesystem.dto.websocket.inbound.BaseMessage;
import com.jake.messagesystem.dto.websocket.inbound.MessageNotification;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.util.JsonUtil;

public class InboundMessageHandler {
    private final TerminalService terminalService;

    public InboundMessageHandler(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    public void handle(String payload) {
        JsonUtil.fromJson(payload, BaseMessage.class)
                .ifPresent(message -> {
                    if (message instanceof MessageNotification messageNotification) {
                        message(messageNotification);
                    }
                });
    }

    private void message(MessageNotification messageNotification) {
        terminalService.printMessage(messageNotification.getUsername(), messageNotification.getContent());
    }
}
