package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.KeyPrefix;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.MessageSeqId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MessageSeqIdGenerator {
    private final Logger log = LoggerFactory.getLogger(MessageSeqIdGenerator.class);

    private final StringRedisTemplate stringRedisTemplate;

    public MessageSeqIdGenerator(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Optional<MessageSeqId> getNext(ChannelId channelId) {
        String seqIdKey = buildMessageSeqIdKey(channelId.id());

        try {
            return Optional.of(new MessageSeqId(stringRedisTemplate.opsForValue().increment(seqIdKey)));
        } catch (Exception e) {
            log.error("Redis set failed. key: {}, cause: {}", seqIdKey, e.getMessage());
        }

        return Optional.empty();
    }

    private String buildMessageSeqIdKey(Long channelId) {
        return "%s:%d:seq_id".formatted(KeyPrefix.CHANNEL, channelId);
    }
}
