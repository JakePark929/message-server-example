package com.jake.messagesystem.handler;

import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.websocket.outbound.AcceptRequest;
import com.jake.messagesystem.dto.websocket.outbound.CreateRequest;
import com.jake.messagesystem.dto.websocket.outbound.DisconnectRequest;
import com.jake.messagesystem.dto.websocket.outbound.EnterRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchChannelInviteCodeRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchChannelsRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchConnectionsRequest;
import com.jake.messagesystem.dto.websocket.outbound.FetchUserInviteCodeRequest;
import com.jake.messagesystem.dto.websocket.outbound.InviteRequest;
import com.jake.messagesystem.dto.websocket.outbound.JoinRequest;
import com.jake.messagesystem.dto.websocket.outbound.LeaveRequest;
import com.jake.messagesystem.dto.websocket.outbound.QuitRequest;
import com.jake.messagesystem.dto.websocket.outbound.RejectRequest;
import com.jake.messagesystem.service.RestApiService;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.service.UserService;
import com.jake.messagesystem.service.WebSocketService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CommandHandler {
    private final RestApiService restApiService;
    private final WebSocketService webSocketService;
    private final TerminalService terminalService;
    private final UserService userService;
    private final Map<String, Function<String[], Boolean>> commands = new HashMap<>();

    public CommandHandler(RestApiService restApiService, WebSocketService webSocketService, TerminalService terminalService, UserService userService) {
        this.restApiService = restApiService;
        this.webSocketService = webSocketService;
        this.terminalService = terminalService;
        this.userService = userService;
        prepareCommands();
    }

    public boolean process(String command, String argument) {
        final Function<String[], Boolean> commander = commands.getOrDefault(command, (ignored) -> {
            terminalService.printSystemMessage("Invalid command: %s".formatted(command));

            return true;
        });

        return commander.apply(argument.split(" "));
    }

    private void prepareCommands() {
        commands.put("register", this::register);
        commands.put("unregister", this::unregister);
        commands.put("login", this::login);
        commands.put("logout", this::logout);

        commands.put("invite-code", this::inviteCode);
        commands.put("invite", this::invite);
        commands.put("accept", this::accept);
        commands.put("reject", this::reject);
        commands.put("disconnect", this::disconnect);
        commands.put("connections", this::connections);
        commands.put("pending", this::pending);
        commands.put("channels", this::channels);
        commands.put("create", this::create);
        commands.put("join", this::join);
        commands.put("enter", this::enter);
        commands.put("leave", this::leave);
        commands.put("quit", this::quit);

        commands.put("clear", this::clear);
        commands.put("exit", this::exit);
        commands.put("help", this::help);
    }

    private Boolean register(String[] params) {
        if (userService.isInLobby() && params.length > 1) {
            if (restApiService.register(params[0], params[1])) {
                terminalService.printSystemMessage("Registered.");
            } else {
                terminalService.printSystemMessage("Register failed");
            }
        }

        return true;
    }

    private Boolean unregister(String[] params) {
        if (userService.isInLobby()) {
            webSocketService.closeSession();
            if (restApiService.unregister()) {
                terminalService.printSystemMessage("Unregistered.");
            } else {
                terminalService.printSystemMessage("Unregister failed.");
            }
        }

        return true;
    }

    private Boolean login(String[] params) {
        if (userService.isInLobby() && params.length > 1) {
            if (restApiService.login(params[0], params[1])) {
                if (webSocketService.createSession(restApiService.getSessionId())) {
                    userService.login(params[0]);
                    terminalService.printSystemMessage("Login successful.");
                } else {
                    terminalService.printSystemMessage("Login failed.");
                }
            }
        }

        return true;
    }

    private Boolean logout(String[] params) {
        webSocketService.closeSession();

        if (restApiService.logout()) {
            userService.logout();
            terminalService.printSystemMessage("Logout successful.");
        } else {
            terminalService.printSystemMessage("Logout failed.");
        }

        return true;
    }

    private Boolean inviteCode(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            if ("user".equals(params[0])) {
                webSocketService.sendMessage(new FetchUserInviteCodeRequest());
                terminalService.printSystemMessage("Get invite code for mine.");
            } else if ("channel".equals(params[0]) && params.length > 1) {
                webSocketService.sendMessage(new FetchChannelInviteCodeRequest(new ChannelId(Long.valueOf(params[1]))));
                terminalService.printSystemMessage("Get invite code for channel.");
            }
        }

        return true;
    }

    private Boolean invite(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            webSocketService.sendMessage(new InviteRequest(new InviteCode(params[0])));
            terminalService.printSystemMessage("Invite user.");
        }

        return true;
    }

    private Boolean accept(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            webSocketService.sendMessage(new AcceptRequest(params[0]));
            terminalService.printSystemMessage("Accept user invite.");
        }

        return true;
    }

    private Boolean reject(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            webSocketService.sendMessage(new RejectRequest(params[0]));
            terminalService.printSystemMessage("Reject user invite.");
        }

        return true;
    }

    private Boolean disconnect(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            webSocketService.sendMessage(new DisconnectRequest(params[0]));
            terminalService.printSystemMessage("Disconnect user.");
        }

        return true;
    }

    private Boolean connections(String[] params) {
        if (userService.isInLobby()) {
            webSocketService.sendMessage(new FetchConnectionsRequest(UserConnectionStatus.ACCEPTED));
            terminalService.printSystemMessage("Get connection list.");
        }

        return true;
    }

    private Boolean pending(String[] params) {
        if (userService.isInLobby()) {
            webSocketService.sendMessage(new FetchConnectionsRequest(UserConnectionStatus.PENDING));
            terminalService.printSystemMessage("Get pending list.");
        }

        return true;
    }

    private Boolean channels(String[] params) {
        if (userService.isInLobby()) {
            webSocketService.sendMessage(new FetchChannelsRequest());
            terminalService.printSystemMessage("Request channels.");
        }

        return true;
    }

    private Boolean create(String[] params) {
        if (userService.isInLobby() && params.length > 1 && params.length < 100) {
            webSocketService.sendMessage(new CreateRequest(params[0], List.of(Arrays.copyOfRange(params, 1, params.length))));
            terminalService.printSystemMessage("Request create channel.");
        } else {
            terminalService.printSystemMessage("Only 1 to 99 users can be included.");
        }

        return true;
    }

    private Boolean join(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            webSocketService.sendMessage(new JoinRequest(new InviteCode(params[0])));
            terminalService.printSystemMessage("Request join channel.");
        }

        return true;
    }

    private Boolean enter(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            webSocketService.sendMessage(new EnterRequest(new ChannelId(Long.valueOf(params[0]))));
            terminalService.printSystemMessage("Request enter channel.");
        }

        return true;
    }

    private Boolean leave(String[] params) {
        if (userService.isInChannel()) {
            webSocketService.sendMessage(new LeaveRequest());
            terminalService.printSystemMessage("Request leave channel.");
        }

        return true;
    }

    private Boolean quit(String[] params) {
        if (userService.isInLobby() && params.length > 0) {
            webSocketService.sendMessage(new QuitRequest(new ChannelId(Long.valueOf(params[0]))));
            terminalService.printSystemMessage("Request quit channel.");
        }

        return true;
    }

    private Boolean clear(String[] params) {
        terminalService.clearTerminal();
        terminalService.printSystemMessage("Terminal cleared.");

        return true;
    }

    private Boolean exit(String[] params) {
        logout(params);
        terminalService.printSystemMessage("Exit message client.");

        return false;
    }

    private Boolean help(String[] params) {
        terminalService.printSystemMessage(
                """
                        === Commands For Lobby ===
                        - /register     Register a new user.                            ex) /register <Username> <Password>
                        - /unregister   Unregister current user.                        ex) /unregister
                        - /login        Login.                                          ex) /login <Username> <Password>
                        - /invite-code  Get the invite code of mine or joined channel.  ex) /invite-code user or /invite-code channel <ChannelId>
                        - /invite       Invite a user to connect.                       ex) /invite <Invite Code>
                        - /accept       Accept the invite request received.             ex) /accept <Inviter Username>
                        - /reject       Reject the invite request received.             ex) /reject <Inviter Username>
                        - /disconnect   Disconnect user.                                ex) /disconnect <Connected Username>
                        - /connections  View the list of connected users.               ex) /connections
                        - /pending      View the list of pending invites.               ex) /pending
                        - /channels     View the list of joined channels.               ex) /channels
                        - /create       Create a channel. (Up to 99 users)              ex) /create <Title> <Username1> <Username2> ...
                        - /join         Join the channel.                               ex) /join <InviteCode>
                        - /enter        Enter the channel.                              ex) /enter <ChannelId>
                        - /quit         Quit the channel.                               ex) /quit <ChannelId>
                        
                        === Commands For Channel ===
                        - /leave        Leave the channel.                              ex) /leave
                        
                        === Commands For Lobby/Channel ===
                        - /logout       Logout.                   ex) /logout
                        - /clear        Clear the terminal.       ex) /clear
                        - /exit         Exit the client.          ex) /exit
                        ====================
                        """
        );

        return true;
    }
}
