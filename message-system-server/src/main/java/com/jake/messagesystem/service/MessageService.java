package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.entity.MessageEntity;
import com.jake.messagesystem.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    public MessageService(ChannelService channelService, MessageRepository messageRepository) {
        this.channelService = channelService;
        this.messageRepository = messageRepository;
    }

    public void sendMessage(UserId senderUserId, String content, ChannelId channelId, Consumer<UserId> messageSender) {
        try {
            messageRepository.save(
                    new MessageEntity(senderUserId.id(), content)
            );
        } catch (Exception e) {
            log.error("Send message failed. cause: {}", e.getMessage());

            return;
        }

        final List<UserId> participantIds = channelService.getParticipantIds(channelId);
        participantIds.stream()
                .filter(userId -> !senderUserId.equals(userId))
                .forEach(participantId -> {
                    if (channelService.isOnline(participantId, channelId)) {
                        messageSender.accept(participantId);
                    }
                });
    }
}
