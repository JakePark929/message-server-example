package com.jake.messagesystem;

import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.handler.WebSocketMessageHandler;
import com.jake.messagesystem.handler.WebSocketSender;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.service.WebSocketService;

public class MessageClient {
    public static void main(String[] args) {
        final String BASE_URL = "localhost:8080";
        final String WEBSOCKET_ENDPOINT = "/ws/v1/message";

        TerminalService terminalService;

        try {
            terminalService = TerminalService.create();
        } catch (Exception e) {
            System.err.println("Failed to run MessageClient: " + e.getMessage());
            return;
        }

        WebSocketSender webSocketSender = new WebSocketSender(terminalService);
        WebSocketService webSocketService = new WebSocketService(terminalService, webSocketSender, BASE_URL, WEBSOCKET_ENDPOINT);
        webSocketService.setWebSocketMessageHandler(new WebSocketMessageHandler(terminalService));

        terminalService.printSystemMessage("채팅 클라이언트 시작! /exit 로 종료, /clear 로 화면 지우기");

        while (true) {
            String input = terminalService.readLine("Enter message: ");

            if (!input.isEmpty() && input.charAt(0) == '/') {
                String command = input.substring(1).trim();

                boolean exit = switch (command) {
                    case "exit" -> {
                        webSocketService.closeSession();
                        yield true;
                    }

                    case "clear" -> {
                        terminalService.clearTerminal();
                        yield false;
                    }

                    case "connect" -> {
                        webSocketService.createSession();
                        yield false;
                    }

                    default -> {
                        yield false;
                    }
                };

                if (exit) {
                    break;
                }
            } else if (!input.isEmpty()) {
                terminalService.printMessage("<me>", input.trim());
                webSocketService.sendMessage(new Message("test client", input));
            }
        }
    }
}
