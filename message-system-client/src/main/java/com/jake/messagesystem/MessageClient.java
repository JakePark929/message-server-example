package com.jake.messagesystem;

import com.jake.messagesystem.service.TerminalService;

public class MessageClient {
    public static void main(String[] args) {
        TerminalService terminalService;

        try {
            terminalService = TerminalService.create();
        } catch (Exception e) {
            System.err.println("Failed to run MessageClient: " + e.getMessage());
            return;
        }

        terminalService.printSystemMessage("채팅 클라이언트 시작! /exit 로 종료, /clear 로 화면 지우기");

        while (true) {
            String input = terminalService.readLine("Enter message: ");

            if (input == null || input.isBlank()) {
                continue;
            }

            if (input.charAt(0) == '/') {
                String command = input.substring(1);

                switch (command) {
                    case "exit":
                        terminalService.printSystemMessage("종료합니다.");
                        return;
                    case "clear":
                        terminalService.clearTerminal();
                        terminalService.printSystemMessage("화면 초기화 완료");
                        break;
                    default:
                        terminalService.printSystemMessage("알 수 없는 명령어: " + command);
                }
            } else {
                terminalService.printMessage("나", input);
            }
        }
    }
}