package com.jake.messagesystem.repository;

import com.jake.messagesystem.dto.projection.ChannelTitleProjection;
import com.jake.messagesystem.dto.projection.InviteCodeProjection;
import com.jake.messagesystem.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    Optional<ChannelTitleProjection> findChanelTitleByChannelId(@NonNull Long channelId);
    Optional<InviteCodeProjection> findChannelInviteCodeByChannelId(@NonNull Long channelId);
}
