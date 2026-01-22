package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.constants.ResultType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.MessageNotificationRecord;
import com.jake.messagesystem.dto.kafka.WriteMessageAckRecord;
import com.jake.messagesystem.dto.kafka.WriteMessageRecord;
import com.jake.messagesystem.dto.projection.MessageInfoProjection;
import com.jake.messagesystem.kafka.KafkaProducer;
import com.jake.messagesystem.repository.UserChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final KafkaProducer kafkaProducer;

    private final PushService pushService;
    private final UserService userService;
    private final ChannelService channelService;
    private final SessionService sessionService;
    private final MessageShardService messageShardService;
    private final UserChannelRepository userChannelRepository;

    public MessageService(
            KafkaProducer kafkaProducer,
            PushService pushService,
            UserService userService,
            ChannelService channelService,
            SessionService sessionService,
            MessageShardService messageShardService,
            UserChannelRepository userChannelRepository
    ) {
        this.kafkaProducer = kafkaProducer;
        this.pushService = pushService;
        this.userService = userService;
        this.channelService = channelService;
        this.sessionService = sessionService;
        this.messageShardService = messageShardService;
        this.userChannelRepository = userChannelRepository;

        pushService.registerPushMessageType(MessageType.NOTIFY_MESSAGE, MessageNotificationRecord.class);
    }

    @Transactional(readOnly = true)
    public Pair<List<Message>, ResultType> getMessages(ChannelId channelId, MessageSeqId startMessageSeqId, MessageSeqId endMessageSeqId) {
        final List<MessageInfoProjection> messageInfos = messageShardService.findByChannelIdAndMessageSequenceBetween(channelId, startMessageSeqId, endMessageSeqId);

        final Set<UserId> userIds = messageInfos.stream().map(projection ->
                new UserId(projection.getUserId())).collect(Collectors.toUnmodifiableSet()
        );
        if (userIds.isEmpty()) {

            return Pair.of(Collections.emptyList(), ResultType.SUCCESS);
        }

        final Pair<Map<UserId, String>, ResultType> result = userService.getUsernames(userIds);
        if (result.getSecond() == ResultType.SUCCESS) {
            final List<Message> messages = messageInfos.stream()
                    .map(projection -> {
                        final UserId userId = new UserId(projection.getUserId());

                        return new Message(
                                channelId,
                                new MessageSeqId(projection.getMessageSequence()),
                                result.getFirst().getOrDefault(userId, "unknown"),
                                projection.getContent()
                        );
                    }).toList();

            return Pair.of(messages, ResultType.SUCCESS);
        } else {
            return Pair.of(Collections.emptyList(), result.getSecond());
        }
    }

    @Transactional
    public void sendMessage(WriteMessageRecord record) {
        ChannelId channelId = record.channelId();
        UserId senderUserId = record.userId();
        MessageSeqId messageSeqId = record.messageSeqId();
        String senderUsername = userService.getUserName(senderUserId).orElse("unknown");

        try {
            messageShardService.save(channelId, messageSeqId, senderUserId, record.content());
        } catch (Exception e) {
            log.error("Send message failed. cause: {}", e.getMessage());

            return;
        }

        final List<UserId> allParticipantIds = channelService.getParticipantIds(channelId);
        final List<UserId> onlineParticipantIds = channelService.getOnlineParticipantIds(channelId, allParticipantIds);
        final Map<String, List<UserId>> listenTopics = sessionService.getListenTopics(onlineParticipantIds);
        allParticipantIds.removeAll(onlineParticipantIds);

        listenTopics.forEach(
                (listenTopic, participantIds) -> {
                    if (participantIds.contains(senderUserId)) {
                        updateLastReadMsgSeq(senderUserId, channelId, messageSeqId);
                        kafkaProducer.sendMessageUsingPartitionKey(
                                listenTopic,
                                channelId,
                                senderUserId,
                                new WriteMessageAckRecord(senderUserId, record.serial(), messageSeqId)
                        );
                        participantIds.remove(senderUserId);
                    } else {
                        kafkaProducer.sendMessageUsingPartitionKey(
                                listenTopic,
                                channelId,
                                senderUserId,
                                new MessageNotificationRecord(
                                        senderUserId,
                                        channelId,
                                        messageSeqId,
                                        senderUsername,
                                        record.content(),
                                        participantIds
                                )
                        );
                    }
                }
        );

        if (!allParticipantIds.isEmpty()) {
            pushService.pushMessage(
                    new MessageNotificationRecord(
                            senderUserId,
                            channelId,
                            messageSeqId,
                            senderUsername,
                            record.content(),
                            allParticipantIds
                    )
            );
        }
    }

    @Transactional
    public void updateLastReadMsgSeq(UserId userId, ChannelId channelId, MessageSeqId messageSeqId) {
        if (userChannelRepository.updateLastReadMsgSeqByUserIdAndChannelId(userId.id(), channelId.id(), messageSeqId.id()) == 0) {
            log.error("Update lastReadMsgSeq failed. No record found for UserId: {} and ChannelId: {}", userId.id(), channelId.id());
        }
    }
}
