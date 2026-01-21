package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.QuitRequestRecord;
import com.jake.messagesystem.dto.kafka.QuitResponseRecord;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class QuitRequestRecordHandler implements BaseRecordHandler<QuitRequestRecord> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public QuitRequestRecordHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(QuitRequestRecord record) {
        final UserId senderUserId = record.userId();

        final ResultType quit;
        try {
            quit = channelService.quit(record.channelId(), senderUserId);
        } catch (Exception e) {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.QUIT_REQUEST, ResultType.FAILED.getMessage()));

            return;
        }

        if (quit == ResultType.SUCCESS) {
            clientNotificationService.sendMessage(senderUserId, new QuitResponseRecord(senderUserId, record.channelId()));
        } else {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.QUIT_REQUEST, quit.getMessage()));
        }
    }
}
