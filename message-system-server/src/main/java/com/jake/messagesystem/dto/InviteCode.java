package com.jake.messagesystem.dto;

public record InviteCode(String code) {
    public InviteCode {
        if (code == null || code.isEmpty()) throw new IllegalArgumentException("Invalid InviteCode");
    }
}
