package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.Channel;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.JoinRequestRecord;
import com.jake.messagesystem.dto.kafka.JoinResponseRecord;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JoinRequestRecordHandler implements BaseRecordHandler<JoinRequestRecord> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public JoinRequestRecordHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(JoinRequestRecord record) {
        final UserId senderUserId = record.userId();

        final Pair<Optional<Channel>, ResultType> join;
        try {
            join = channelService.join(record.inviteCode(), senderUserId);
        } catch (Exception e) {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.JOIN_REQUEST, ResultType.FAILED.getMessage()));

            return;
        }

        join.getFirst().ifPresentOrElse(
                channel -> clientNotificationService.sendMessage(senderUserId, new JoinResponseRecord(senderUserId, channel.channelId(), channel.title())),
                () -> clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.JOIN_REQUEST, join.getSecond().getMessage()))
        );
    }
}
