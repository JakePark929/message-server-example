package com.jake.messagesystem.handler.kafka;

import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.FetchChannelsRequestRecord;
import com.jake.messagesystem.dto.kafka.FetchChannelsResponseRecord;
import com.jake.messagesystem.service.ChannelService;
import com.jake.messagesystem.service.ClientNotificationService;
import org.springframework.stereotype.Component;

@Component
public class FetchChannelsRequestRecordHandler implements BaseRecordHandler<FetchChannelsRequestRecord> {
    private final ChannelService channelService;
    private final ClientNotificationService clientNotificationService;

    public FetchChannelsRequestRecordHandler(ChannelService channelService, ClientNotificationService clientNotificationService) {
        this.channelService = channelService;
        this.clientNotificationService = clientNotificationService;
    }

    @Override
    public void handleRecord(FetchChannelsRequestRecord record) {
        final UserId senderUserId = record.userId();

        clientNotificationService.sendMessage(senderUserId, new FetchChannelsResponseRecord(senderUserId, channelService.getChannels(senderUserId)));
    }
}
