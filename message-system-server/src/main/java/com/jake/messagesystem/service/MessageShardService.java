package com.jake.messagesystem.service;

import com.jake.messagesystem.db.ShardContext;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.projection.MessageInfoProjection;
import com.jake.messagesystem.entity.MessageEntity;
import com.jake.messagesystem.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageShardService {
    private final MessageRepository messageRepository;

    public MessageShardService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public MessageSeqId findLastMessageSequenceByChannelId(ChannelId channelId) {
        try (ShardContext.ShardContextScope ignored = new ShardContext.ShardContextScope(channelId.id())) {

            return messageRepository.findLastMessageSequenceByChannelId(channelId.id())
                    .map(MessageSeqId::new)
                    .orElse(new MessageSeqId(0L));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<MessageInfoProjection> findByChannelIdAndMessageSequenceBetween(ChannelId channelId, MessageSeqId startMessageSeqId, MessageSeqId endMessageSeqId) {
        try (ShardContext.ShardContextScope ignored = new ShardContext.ShardContextScope(channelId.id())) {

            return messageRepository.findByChannelIdAndMessageSequenceBetween(channelId.id(), startMessageSeqId.id(), endMessageSeqId.id());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(ChannelId channelId, MessageSeqId messageSeqId, UserId senderUserId, String content) {
        try (ShardContext.ShardContextScope ignored = new ShardContext.ShardContextScope(channelId.id())) {
            messageRepository.save(new MessageEntity(channelId.id(), messageSeqId.id(), senderUserId.id(), content));
        }
    }
}
