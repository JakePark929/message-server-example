package com.jake.messagesystem.repository;

import com.jake.messagesystem.dto.projection.MessageInfoProjection;
import com.jake.messagesystem.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query(
            "SELECT MAX(m.messageSequence) " +
                    "FROM MessageEntity m " +
                    "WHERE m.channelId = :channelId"
    )
    Optional<Long> findLastMessageSequenceByChannelId(@Param("channelId") Long channelId);

    List<MessageInfoProjection> findByChannelIdAndMessageSequenceBetween(
            @NonNull Long channelId,
            @NonNull Long startMessageSequence,
            @NonNull Long endMessageSequence
    );
}
