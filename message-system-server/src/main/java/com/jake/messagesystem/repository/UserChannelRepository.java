package com.jake.messagesystem.repository;

import com.jake.messagesystem.dto.projection.ChannelProjection;
import com.jake.messagesystem.dto.projection.UserIdProjection;
import com.jake.messagesystem.entity.UserChannelEntity;
import com.jake.messagesystem.entity.UserChannelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChannelRepository extends JpaRepository<UserChannelEntity, UserChannelId> {
    boolean existsByUserIdAndChannelId(@NonNull Long userId, @NonNull Long channelId);
    List<UserIdProjection> findUserIdsByChannelId(@NonNull Long channelId);

    @Query("SELECT c.channelId AS channelId, c.title AS title, c.headCount AS headCount " +
            "FROM UserChannelEntity uc " +
            "INNER JOIN ChannelEntity c ON uc.channelId = c.channelId " +
            "WHERE uc.userId = :userId"
    )
    List<ChannelProjection> findChannelsByUserId(@NonNull @Param("userId") Long userId);

    void deleteByUserIdAndChannelId(@NonNull Long userId, @NonNull Long channelId);
}
