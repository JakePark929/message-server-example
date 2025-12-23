package com.jake.messagesystem.dto;

public record ChannelId(Long id) {
    public ChannelId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ChannelId");
        }
    }
}
