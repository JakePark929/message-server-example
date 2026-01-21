package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.FetchMessagesRequestRecord;
import com.jake.messagesystem.dto.kafka.FetchMessagesResponseRecord;
import com.jake.messagesystem.service.ClientNotificationService;
import com.jake.messagesystem.service.MessageService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FetchMessagesRequestRecordHandler implements BaseRecordHandler<FetchMessagesRequestRecord> {
    private final MessageService messageService;
    private final ClientNotificationService clientNotificationService;

    public FetchMessagesRequestRecordHandler(MessageService messageService, ClientNotificationService clientNotificationService) {
        this.messageService = messageService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchMessagesRequestRecord record) {
        final UserId senderUserId = record.userId();
        final ChannelId channelId = record.channelId();

        final Pair<List<Message>, ResultType> result = messageService.getMessages(channelId, record.startMessageSeqId(), record.endMessageSeqId());

        if (result.getSecond() == ResultType.SUCCESS) {
            final List<Message> messages = result.getFirst();
            clientNotificationService.sendMessageUsingPartitionKey(channelId, senderUserId, new FetchMessagesResponseRecord(senderUserId, channelId, messages));
        } else {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.FETCH_MESSAGES_REQUEST, result.getSecond().getMessage()));
        }
    }
}
