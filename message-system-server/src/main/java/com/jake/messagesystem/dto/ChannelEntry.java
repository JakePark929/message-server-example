package com.jake.messagesystem.dto;

public record ChannelEntry(String title, MessageSeqId lastReadMessageSeqId, MessageSeqId lastChannelMessageSeqId) {
}
