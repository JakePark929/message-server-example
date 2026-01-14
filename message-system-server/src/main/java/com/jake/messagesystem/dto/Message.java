package com.jake.messagesystem.dto;

public record Message(ChannelId channelId, MessageSeqId messageSeqId, String username, String content) {
}
