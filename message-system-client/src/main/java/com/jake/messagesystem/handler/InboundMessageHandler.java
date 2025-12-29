package com.jake.messagesystem.handler;

import com.jake.messagesystem.dto.websocket.inbound.AcceptNotification;
import com.jake.messagesystem.dto.websocket.inbound.AcceptResponse;
import com.jake.messagesystem.dto.websocket.inbound.BaseMessage;
import com.jake.messagesystem.dto.websocket.inbound.CreateResponse;
import com.jake.messagesystem.dto.websocket.inbound.DisconnectResponse;
import com.jake.messagesystem.dto.websocket.inbound.EnterResponse;
import com.jake.messagesystem.dto.websocket.inbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.inbound.FetchChannelInviteCodeResponse;
import com.jake.messagesystem.dto.websocket.inbound.FetchChannelsResponse;
import com.jake.messagesystem.dto.websocket.inbound.FetchConnectionsResponse;
import com.jake.messagesystem.dto.websocket.inbound.FetchUserInviteCodeResponse;
import com.jake.messagesystem.dto.websocket.inbound.InviteNotification;
import com.jake.messagesystem.dto.websocket.inbound.InviteResponse;
import com.jake.messagesystem.dto.websocket.inbound.JoinNotification;
import com.jake.messagesystem.dto.websocket.inbound.JoinResponse;
import com.jake.messagesystem.dto.websocket.inbound.LeaveResponse;
import com.jake.messagesystem.dto.websocket.inbound.MessageNotification;
import com.jake.messagesystem.dto.websocket.inbound.QuitResponse;
import com.jake.messagesystem.dto.websocket.inbound.RejectResponse;
import com.jake.messagesystem.service.TerminalService;
import com.jake.messagesystem.service.UserService;
import com.jake.messagesystem.util.JsonUtil;

public class InboundMessageHandler {
    private final TerminalService terminalService;
    private final UserService userService;

    public InboundMessageHandler(TerminalService terminalService, UserService userService) {
        this.terminalService = terminalService;
        this.userService = userService;
    }

    public void handle(String payload) {
        JsonUtil.fromJson(payload, BaseMessage.class)
                .ifPresent(message -> {
                    if (message instanceof MessageNotification messageNotification) {
                        message(messageNotification);
                    } else if (message instanceof FetchUserInviteCodeResponse fetchUserInviteCodeResponse) {
                        fetchUserInviteCode(fetchUserInviteCodeResponse);
                    } else if (message instanceof FetchChannelInviteCodeResponse fetchChannelInviteCodeResponse) {
                        fetchChannelInviteCode(fetchChannelInviteCodeResponse);
                    } else if (message instanceof InviteResponse inviteResponse) {
                        invite(inviteResponse);
                    } else if (message instanceof InviteNotification inviteNotification) {
                        askInvite(inviteNotification);
                    } else if (message instanceof AcceptResponse acceptResponse) {
                        accept(acceptResponse);
                    } else if (message instanceof AcceptNotification acceptNotification) {
                        acceptNotification(acceptNotification);
                    } else if (message instanceof RejectResponse rejectResponse) {
                        reject(rejectResponse);
                    } else if (message instanceof DisconnectResponse disconnectResponse) {
                        disconnect(disconnectResponse);
                    } else if (message instanceof FetchConnectionsResponse fetchConnectionsResponse) {
                        fetchConnections(fetchConnectionsResponse);
                    } else if (message instanceof FetchChannelsResponse channelsResponse) {
                        fetchChannels(channelsResponse);
                    } else if (message instanceof CreateResponse createResponse) {
                        create(createResponse);
                    } else if (message instanceof JoinResponse joinResponse) {
                        join(joinResponse);
                    } else if (message instanceof JoinNotification joinNotification) {
                        joinNotification(joinNotification);
                    } else if (message instanceof EnterResponse enterResponse) {
                        enter(enterResponse);
                    } else if (message instanceof LeaveResponse leaveResponse) {
                        leave(leaveResponse);
                    } else if (message instanceof QuitResponse quitResponse) {
                        quit(quitResponse);
                    } else if (message instanceof ErrorResponse errorResponse) {
                        error(errorResponse);
                    }
                });
    }

    private void message(MessageNotification messageNotification) {
        terminalService.printMessage(messageNotification.getUsername(), messageNotification.getContent());
    }

    private void fetchUserInviteCode(FetchUserInviteCodeResponse fetchUserInviteCodeResponse) {
        terminalService.printSystemMessage("My Invite Code: %s".formatted(fetchUserInviteCodeResponse.getInviteCode()));
    }

    private void fetchChannelInviteCode(FetchChannelInviteCodeResponse fetchChannelInviteCodeResponse) {
        terminalService.printSystemMessage("%s Invite Code: %s".formatted(fetchChannelInviteCodeResponse.getChannelId(), fetchChannelInviteCodeResponse.getInviteCode()));
    }

    private void invite(InviteResponse inviteResponse) {
        terminalService.printSystemMessage("Invite %s result: %s".formatted(inviteResponse.getInviteCode(), inviteResponse.getStatus()));
    }

    private void askInvite(InviteNotification inviteNotification) {
        terminalService.printSystemMessage("Do you accept %s's connection request?".formatted(inviteNotification.getUsername()));
    }

    private void accept(AcceptResponse acceptResponse) {
        terminalService.printSystemMessage("Connected %s".formatted(acceptResponse.getUsername()));
    }

    private void acceptNotification(AcceptNotification acceptNotification) {
        terminalService.printSystemMessage("Connected %s".formatted(acceptNotification.getUsername()));
    }

    private void reject(RejectResponse rejectResponse) {
        terminalService.printSystemMessage("Reject %s result: %s".formatted(rejectResponse.getUsername(), rejectResponse.getStatus()));
    }

    private void disconnect(DisconnectResponse disconnectResponse) {
        terminalService.printSystemMessage("Disconnected %s result : %s".formatted(disconnectResponse.getUsername(), disconnectResponse.getStatus()));
    }

    private void fetchConnections(FetchConnectionsResponse fetchConnectionsResponse) {
        fetchConnectionsResponse.getConnections().forEach(connection ->
                terminalService.printSystemMessage("%s : %s".formatted(connection.username(), connection.status()))
        );
    }

    private void fetchChannels(FetchChannelsResponse fetchConnectionsResponse) {
        fetchConnectionsResponse.getChannels().forEach(channel ->
                terminalService.printSystemMessage("%s : %s (%d)".formatted(channel.channelId(), channel.title(), channel.headCount()))
        );
    }

    private void create(CreateResponse createResponse) {
        terminalService.printSystemMessage("Create channel %s: %s".formatted(createResponse.getChannelId(), createResponse.getTitle()));
    }

    private void join(JoinResponse joinResponse) {
        terminalService.printSystemMessage("Joined channel %s: %s".formatted(joinResponse.getChannelId(), joinResponse.getTitle()));
    }

    private void joinNotification(JoinNotification joinNotification) {
        terminalService.printSystemMessage("Joined channel %s: %s".formatted(joinNotification.getChannelId(), joinNotification.getTitle()));
    }

    private void enter(EnterResponse enterResponse) {
        userService.moveToChannel(enterResponse.getChannelId());
        terminalService.printSystemMessage("Enter channel %s: %s".formatted(enterResponse.getChannelId(), enterResponse.getTitle()));
    }

    private void leave(LeaveResponse leaveResponse) {
        terminalService.printSystemMessage("Leave channel %s.".formatted(userService.getChannelId()));
        userService.moveToLobby();
    }

    private void quit(QuitResponse quitResponse) {
        terminalService.printSystemMessage("Quit channel %s.".formatted(quitResponse.getChannelId()));
    }

    private void error(ErrorResponse errorResponse) {
        terminalService.printSystemMessage("Error %s: %s".formatted(errorResponse.getMessageType(), errorResponse.getMessage()));
    }
}
