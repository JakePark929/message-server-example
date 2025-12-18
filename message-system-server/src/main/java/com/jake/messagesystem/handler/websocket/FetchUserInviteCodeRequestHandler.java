package com.jake.messagesystem.handler.websocket;

import com.jake.messagesystem.constants.Constants;
import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.websocket.inbound.FetchUserInviteCodeRequest;
import com.jake.messagesystem.dto.websocket.outbound.ErrorResponse;
import com.jake.messagesystem.dto.websocket.outbound.FetchUserInviteCodeResponse;
import com.jake.messagesystem.service.UserService;
import com.jake.messagesystem.session.WebSocketSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FetchUserInviteCodeRequestHandler implements BaseRequestHandler<FetchUserInviteCodeRequest> {
    private final UserService userService;
    private final WebSocketSessionManager webSocketSessionManager;

    public FetchUserInviteCodeRequestHandler(UserService userService, WebSocketSessionManager webSocketSessionManager) {
        this.userService = userService;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    @Override
    public void handleRequest(WebSocketSession senderSession, FetchUserInviteCodeRequest request) {
        final UserId senderUserId = (UserId) senderSession.getAttributes().get(Constants.USER_ID.getValue());
        userService.getInviteCode(senderUserId).ifPresentOrElse(
                inviteCode -> webSocketSessionManager.sendMessage(senderSession, new FetchUserInviteCodeResponse(inviteCode)),
                () -> webSocketSessionManager.sendMessage(senderSession, new ErrorResponse(MessageType.FETCH_USER_INVITE_CODE_REQUEST, "Fetch user invite code failed."))
        );
    }
}
