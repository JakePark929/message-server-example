package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.Channel;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.CreateRequestRecord;
import com.jake.messagesystem.dto.kafka.CreateResponseRecord;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.JoinNotificationRecord;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.UserService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class CreateRequestRecordHandler implements BaseRecordHandler<CreateRequestRecord> {
    private final ChannelService channelService;
    private final UserService userService;
    private final ClientNotificationService clientNotificationService;

    public CreateRequestRecordHandler(ChannelService channelService, UserService userService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.userService = userService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(CreateRequestRecord record) {
        final UserId senderUserId = record.userId();

        final List<UserId> participantIds = userService.getUserIds(record.participantUsernames());
        if (participantIds.isEmpty()) {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.CREATE_REQUEST, ResultType.NOT_FOUND.getMessage()));

            return;
        }

        Pair<Optional<Channel>, ResultType> create;
        try {
            create = channelService.create(senderUserId, participantIds, record.title());
        } catch (Exception e) {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.CREATE_REQUEST, ResultType.FAILED.getMessage()));

            return;
        }

        if (create.getFirst().isEmpty()) {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.CREATE_REQUEST, create.getSecond().getMessage()));

            return;
        }

        final Channel channel = create.getFirst().get();
        clientNotificationService.sendMessage(senderUserId, new CreateResponseRecord(senderUserId, channel.channelId(), channel.title()));
        participantIds.forEach(participantId ->
                CompletableFuture.runAsync(() ->
                        clientNotificationService.sendMessage(participantId, new JoinNotificationRecord(participantId, channel.channelId(), channel.title()))
                )
        );
    }
}
