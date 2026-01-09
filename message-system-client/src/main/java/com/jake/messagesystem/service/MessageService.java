package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.Message;
import com.jake.messagesystem.dto.MessageSeqId;
import com.jake.messagesystem.dto.websocket.inbound.FetchMessagesResponse;
import com.jake.messagesystem.dto.websocket.inbound.MessageNotification;
import com.jake.messagesystem.dto.websocket.inbound.WriteMessageAck;
import com.jake.messagesystem.dto.websocket.outbound.FetchMessagesRequest;
import com.jake.messagesystem.dto.websocket.outbound.WriteMessage;
import com.jake.messagesystem.util.JsonUtil;
import jakarta.websocket.Session;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MessageService {
    private static final int LIMIT_RETRIES = 3;
    private static final long TIMEOUT_MS = 3000L;
    private final TerminalService terminalService;
    private final UserService userService;
    private final Map<Long, CompletableFuture<WriteMessageAck>> pendingMessages = new ConcurrentHashMap<>();
    private final Map<MessageSeqIdRange, ScheduledFuture<?>> scheduledFetchMessagesRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private WebSocketService webSocketService;
            
    public MessageService(TerminalService terminalService, UserService userService) {
        this.terminalService = terminalService;
        this.userService = userService;
    }

    public void setWebSocketService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void receiveMessage(WriteMessageAck writeMessageAck) {
        CompletableFuture<WriteMessageAck> future = pendingMessages.get(writeMessageAck.getSerial());

        if (future != null) {
            future.complete(writeMessageAck);
        }
    }

    public void receiveMessage(MessageNotification messageNotification) {
        if (userService.isInLobby() || !userService.getChannelId().equals(messageNotification.getChannelId())) {
            return;
        }

        final MessageSeqId lastReadMessageSeqId = userService.getLastReadMessageSeqId();
        final MessageSeqId receiveMessageSeqId = messageNotification.getMessageSeqId();
        if (lastReadMessageSeqId == null || receiveMessageSeqId.id() == lastReadMessageSeqId.id() + 1) {
            for (MessageSeqIdRange idRange : scheduledFetchMessagesRequests.keySet()) {
                if (receiveMessageSeqId.id() >= idRange.start.id() && receiveMessageSeqId.id() <= idRange.end.id()) {
                    scheduledFetchMessagesRequests.get(idRange).cancel(false);
                    if (idRange.start.equals(idRange.end)) {
                        reserveFetchMessagesRequest(idRange.start, idRange.end);
                    }
                }
            }

            userService.addMessage(new Message(
                    messageNotification.getChannelId(),
                    messageNotification.getMessageSeqId(),
                    messageNotification.getUsername(),
                    messageNotification.getContent()
            ));

            processMessageBuffer();
        } else if (receiveMessageSeqId.id() > lastReadMessageSeqId.id() + 1) {
            userService.addMessage(new Message(
                    messageNotification.getChannelId(),
                    messageNotification.getMessageSeqId(),
                    messageNotification.getUsername(),
                    messageNotification.getContent()
            ));

            reserveFetchMessagesRequest(lastReadMessageSeqId, receiveMessageSeqId);
        } else if (receiveMessageSeqId.id() <= lastReadMessageSeqId.id()) {
            terminalService.printSystemMessage("Ignore duplication message: " + messageNotification.getMessageSeqId());
        }
    }

    public void receiveMessage(FetchMessagesResponse fetchMessagesResponse) {
        if (userService.isInLobby() || !userService.getChannelId().equals(fetchMessagesResponse.getChannelId())) {
            terminalService.printSystemMessage("Invalid channelId. Ignore message");

            return;
        }

        fetchMessagesResponse.getMessages().forEach(userService::addMessage);
        processMessageBuffer();
    }

    public void sendMessage(Session session, WriteMessage message) {
        sendMessage(session, message, 0);
    }

    public void sendMessage(FetchMessagesRequest fetchMessagesRequest) {
        webSocketService.sendMessage(fetchMessagesRequest);
    }
    
    private void processMessageBuffer() {
        while (!userService.isBufferEmpty()) {
            final Message peekMessage = userService.peekMessage();
            if (userService.getLastReadMessageSeqId() == null || peekMessage.messageSeqId().id() == userService.getLastReadMessageSeqId().id() + 1) {
                final Message message = userService.popMessage();
                terminalService.printMessage(message.username(), message.content());
            } else if (peekMessage.messageSeqId().id() <= userService.getLastReadMessageSeqId().id()) {
                userService.popMessage();
            } else if (peekMessage.messageSeqId().id() > userService.getLastReadMessageSeqId().id() + 1) {
                break;
            }
        }
    }

    private void reserveFetchMessagesRequest(MessageSeqId lastReadSeqId, MessageSeqId receivedSeqId) {
        final MessageSeqIdRange messageSeqIdRange = new MessageSeqIdRange(new MessageSeqId(lastReadSeqId.id() + 1), new MessageSeqId(receivedSeqId.id() - 1));
        final ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(() -> {
            webSocketService.sendMessage(new FetchMessagesRequest(userService.getChannelId(), messageSeqIdRange.start, messageSeqIdRange.end));
            scheduledFetchMessagesRequests.remove(messageSeqIdRange);
        }, 100, TimeUnit.MILLISECONDS);

        scheduledFetchMessagesRequests.put(messageSeqIdRange, scheduledFuture);
    }

    private void sendMessage(Session session, WriteMessage message, int retryCount) {
        if (session != null && session.isOpen()) {
            CompletableFuture<WriteMessageAck> future = new CompletableFuture<>();
            future
                    .orTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .whenCompleteAsync((response, throwable) -> {
                        if (response != null) {
                            userService.setLastReadMessageSeqId(response.getMessageSeqId());
                            terminalService.printMessage("<me>", message.getContent());
                            pendingMessages.remove(message.getSerial());
                        } else if (throwable instanceof TimeoutException && retryCount < LIMIT_RETRIES) {
                            sendMessage(session, message, retryCount + 1);
                        } else {
                            terminalService.printSystemMessage("Send message failed.");
                            pendingMessages.remove(message.getSerial());
                        }
                    });
            pendingMessages.put(message.getSerial(), future);

            JsonUtil.toJson(message).ifPresent(payload -> session.getAsyncRemote().sendText(payload, result -> {
                if (!result.isOK()) {
                    terminalService.printSystemMessage("'%s' send failed. cause: %s".formatted(payload, result.getException()));
                }
            }));
        }
    }
    
    private record MessageSeqIdRange(MessageSeqId start, MessageSeqId end) {}
}
