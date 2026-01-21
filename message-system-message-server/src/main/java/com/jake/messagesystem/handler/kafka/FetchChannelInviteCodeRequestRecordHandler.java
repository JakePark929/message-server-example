package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.FetchChannelInviteCodeRequestRecord;
import com.jake.messagesystem.dto.kafka.FetchChannelInviteCodeResponseRecord;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class FetchChannelInviteCodeRequestRecordHandler implements BaseRecordHandler<FetchChannelInviteCodeRequestRecord> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public FetchChannelInviteCodeRequestRecordHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchChannelInviteCodeRequestRecord record) {
        final UserId senderUserId = record.userId();

        if (!channelService.isJoined(record.channelId(), senderUserId)) {
            clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.FETCH_USER_INVITE_CODE_REQUEST, "Not joined the channel."));

            return;
        }

        channelService.getInviteCode(record.channelId()).ifPresentOrElse(
                inviteCode -> clientNotificationService.sendMessage(senderUserId, new FetchChannelInviteCodeResponseRecord(senderUserId, record.channelId(), inviteCode)),
                () -> clientNotificationService.sendError(new ErrorResponseRecord(senderUserId, MessageType.FETCH_CHANNEL_INVITE_CODE_REQUEST, "Fetch channel invite code failed."))
        );
    }
}
