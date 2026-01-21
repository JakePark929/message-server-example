package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.ChannelEntry;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.EnterRequestRecord;
import com.jake.messagesystem.dto.kafka.EnterResponseRecord;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EnterRequestRecordHandler implements BaseRecordHandler<EnterRequestRecord> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public EnterRequestRecordHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(EnterRequestRecord record) {
        final UserId senderUserId = record.userId();

        final Pair<Optional<ChannelEntry>, ResultType> enter = channelService.enter(record.channelId(), senderUserId);
        enter.getFirst().ifPresentOrElse(
                channelEntry -> clientNotificationService.sendMessage(
                        senderUserId,
                        new EnterResponseRecord(
                                senderUserId,
                                record.channelId(),
                                channelEntry.title(),
                                channelEntry.lastReadMessageSeqId(),
                                channelEntry.lastChannelMessageSeqId()
                        )
                ),
                () -> clientNotificationService.sendError(
                        new ErrorResponseRecord(
                                senderUserId,
                                MessageType.ENTER_REQUEST,
                                enter.getSecond().getMessage()
                        )
                )
        );
    }
}
