package com.jake.messagesystem;

import com.jake.messagesystem.dto.websocket.outbound.WriteMessage;
import com.jake.messagesystem.handler.CommandHandler;
import com.jake.messagesystem.handler.InboundMessageHandler;
import com.jake.messagesystem.handler.WebSocketMessageHandler;
import com.jake.messagesystem.service.MessageService;
import com.jake.messagesystem.service.RestApiService;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.service.UserService;
import com.jake.messagesystem.service.WebSocketService;
import com.jake.messagesystem.util.JsonUtil;

import java.io.IOError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageClient {
    public static void main(String[] args) {
        final List<String> SERVICE_DISCOVERY_URLS = new ArrayList<>(Arrays.asList("localhost:18500", "localhost:18501", "localhost:18502"));
        final String SERVICE_DISCOVERY_ENDPOINT = "/v1/catalog/service/nginx";
        final String WEBSOCKET_ENDPOINT = "/ws/v1/message";

        TerminalService terminalService;
        try {
            terminalService = TerminalService.create();
        } catch (Exception e) {
            System.err.println("Failed to run MessageClient: " + e.getMessage());
            return;
        }

        Collections.shuffle(SERVICE_DISCOVERY_URLS);
        terminalService.printSystemMessage(SERVICE_DISCOVERY_URLS.toString());

        UserService userService = new UserService();
        MessageService messageService = new MessageService(terminalService, userService);
        JsonUtil.setTerminalService(terminalService);
        RestApiService restApiService = new RestApiService(terminalService, SERVICE_DISCOVERY_URLS, SERVICE_DISCOVERY_ENDPOINT);
        WebSocketService webSocketService = new WebSocketService(userService, terminalService, messageService, restApiService.getServerEndpoints(), WEBSOCKET_ENDPOINT);
        InboundMessageHandler inboundMessageHandler = new InboundMessageHandler(terminalService, messageService, userService);
        webSocketService.setWebSocketMessageHandler(new WebSocketMessageHandler(inboundMessageHandler));
        CommandHandler commandHandler = new CommandHandler(restApiService, webSocketService, terminalService, userService);
        messageService.setWebSocketService(webSocketService);

        terminalService.printSystemMessage("Chat client started! Type '/exit' to quit, '/clear' to clear the screen.");
        terminalService.printSystemMessage("'/help' Help for commands. ex) /help");

        while (true) {
            try {
                String input = terminalService.readLine("Enter message: ");

                if (!input.isEmpty() && input.charAt(0) == '/') {
                    String[] parts = input.split(" ", 2);
                    String command = parts[0].substring(1);
                    String argument = parts.length > 1 ? parts[1] : "";

                    if (!commandHandler.process(command, argument)) {
                        break;
                    }
                } else if (!input.isEmpty() && userService.isInChannel()) {
                    webSocketService.sendMessage(new WriteMessage(System.currentTimeMillis(), userService.getChannelId(), input));
                }
            } catch (IOError e) {
                terminalService.flush();
                commandHandler.process("exit","");

                return;
            } catch (NumberFormatException e) {
                terminalService.printSystemMessage("Invalid input: " + e.getMessage());
            }
        }
    }
}
