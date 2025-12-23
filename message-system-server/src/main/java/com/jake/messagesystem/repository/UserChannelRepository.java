package com.jake.messagesystem.repository;

import com.jake.messagesystem.dto.projection.UserIdProjection;
import com.jake.messagesystem.entity.UserChannelEntity;
import com.jake.messagesystem.entity.UserChannelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChannelRepository extends JpaRepository<UserChannelEntity, UserChannelId> {
    boolean existsByUserIdAndChannelId(@NonNull Long userId, @NonNull Long channelId);
    List<UserIdProjection> findUserIdsByChannelId(@NonNull Long channelId);
}
