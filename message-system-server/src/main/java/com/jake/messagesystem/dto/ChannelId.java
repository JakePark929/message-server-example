package com.jake.messagesystem.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public record ChannelId(@JsonValue Long id) {
    public ChannelId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ChannelId");
        }
    }
}
