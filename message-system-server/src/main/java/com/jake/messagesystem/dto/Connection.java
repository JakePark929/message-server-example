package com.jake.messagesystem.dto;

import com.jake.messagesystem.constants.UserConnectionStatus;

public record Connection(String username, UserConnectionStatus status) {}
