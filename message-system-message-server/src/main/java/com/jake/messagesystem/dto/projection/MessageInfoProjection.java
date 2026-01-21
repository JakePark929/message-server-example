package com.jake.messagesystem.dto.projection;

public interface MessageInfoProjection {
    Long getMessageSequence();

    Long getUserId();

    String getContent();
}
