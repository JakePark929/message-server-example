package com.jake.messagesystem.service;

import com.jake.messagesystem.dto.ChannelId;

public class UserService {
    private Location userLocation = Location.LOBBY;
    private String username = "";
    private ChannelId channelId = null;

    public boolean isInLobby() {
        return userLocation == Location.LOBBY;
    }

    public boolean isInChannel() {
        return userLocation == Location.CHANNEL;
    }

    public String getUsername() {
        return username;
    }

    public void login(String username) {
        this.username = username;
        moveToLobby();
    }

    public void logout() {
        this.username = "";
        moveToLobby();
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public void moveToLobby() {
        userLocation = Location.LOBBY;
        this.channelId = null;
    }

    public void moveToChannel(ChannelId channelId) {
        userLocation = Location.CHANNEL;
        this.channelId = channelId;
    }

    private enum Location {
        LOBBY, CHANNEL
    }
}
