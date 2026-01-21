package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.LeaveRequestRecord;
import com.jake.messagesystem.dto.kafka.LeaveResponseRecord;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestRecordHandler implements BaseRecordHandler<LeaveRequestRecord> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public LeaveRequestRecordHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(LeaveRequestRecord record) {
        final UserId senderUserId = record.userId();

        if (channelService.leave(senderUserId)) {
            clientNotificationService.sendMessage(senderUserId, new LeaveResponseRecord(senderUserId));
        } else {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.LEAVE_REQUEST, "Leave failed."));
        }
    }
}
