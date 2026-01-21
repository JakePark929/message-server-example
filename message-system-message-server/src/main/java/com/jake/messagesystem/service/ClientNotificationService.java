package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.MessageType;
import com.jake.messagesystem.dto.ChannelId;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.dto.kafka.AcceptNotificationRecord;
import com.jake.messagesystem.dto.kafka.AcceptResponseRecord;
import com.jake.messagesystem.dto.kafka.CreateResponseRecord;
import com.jake.messagesystem.dto.kafka.DisconnectResponseRecord;
import com.jake.messagesystem.dto.kafka.ErrorResponseRecord;
import com.jake.messagesystem.dto.kafka.InviteNotificationRecord;
import com.jake.messagesystem.dto.kafka.InviteResponseRecord;
import com.jake.messagesystem.dto.kafka.JoinNotificationRecord;
import com.jake.messagesystem.dto.kafka.LeaveResponseRecord;
import com.jake.messagesystem.dto.kafka.QuitResponseRecord;
import com.jake.messagesystem.dto.kafka.RecordInterface;
import com.jake.messagesystem.dto.kafka.RejectResponseRecord;
import com.jake.messagesystem.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClientNotificationService {
    private static final Logger log = LoggerFactory.getLogger(ClientNotificationService.class);

    private final KafkaProducer kafkaProducer;

    private final SessionService sessionService;
    private final PushService pushService;

    public ClientNotificationService(KafkaProducer kafkaProducer, SessionService sessionService, PushService pushService) {
        this.kafkaProducer = kafkaProducer;
        this.sessionService = sessionService;
        this.pushService = pushService;

        pushService.registerPushMessageType(MessageType.INVITE_RESPONSE, InviteResponseRecord.class);
        pushService.registerPushMessageType(MessageType.ASK_INVITE, InviteNotificationRecord.class);
        pushService.registerPushMessageType(MessageType.ACCEPT_RESPONSE, AcceptResponseRecord.class);
        pushService.registerPushMessageType(MessageType.NOTIFY_ACCEPT, AcceptNotificationRecord.class);
        pushService.registerPushMessageType(MessageType.NOTIFY_JOIN, JoinNotificationRecord.class);
        pushService.registerPushMessageType(MessageType.DISCONNECT_RESPONSE, DisconnectResponseRecord.class);
        pushService.registerPushMessageType(MessageType.REJECT_RESPONSE, RejectResponseRecord.class);
        pushService.registerPushMessageType(MessageType.CREATE_RESPONSE, CreateResponseRecord.class);
        pushService.registerPushMessageType(MessageType.LEAVE_RESPONSE, LeaveResponseRecord.class);
        pushService.registerPushMessageType(MessageType.QUIT_RESPONSE, QuitResponseRecord.class);
    }

    public void sendMessage(UserId userId, RecordInterface recordInterface) {
        sessionService.getListenTopic(userId).ifPresentOrElse(
                topic -> kafkaProducer.sendResponse(topic, recordInterface),
                () -> pushService.pushMessage(recordInterface)
        );
    }

    public void sendMessageUsingPartitionKey(ChannelId channelId, UserId userId, RecordInterface recordInterface) {
        sessionService.getListenTopic(userId).ifPresentOrElse(
                topic -> kafkaProducer.sendMessageUsingPartitionKey(topic, channelId, userId, recordInterface),
                () -> pushService.pushMessage(recordInterface)
        );
    }

    public void sendError(ErrorResponseRecord errorRecord) {
        sessionService.getListenTopic(errorRecord.userId()).ifPresentOrElse(
                topic -> kafkaProducer.sendResponse(topic, errorRecord),
                () -> log.warn(
                        "Send error failed. Type: {}, Error: {}, User: {} is offline.",
                        errorRecord.messageType(),
                        errorRecord.message(),
                        errorRecord.userId()
                )
        );
    }
}
