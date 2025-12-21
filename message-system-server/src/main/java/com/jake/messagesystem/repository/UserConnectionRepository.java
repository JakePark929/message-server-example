package com.jake.messagesystem.repository;

import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.projection.InviterUserIdProjection;
import com.jake.messagesystem.dto.projection.UserConnectionStatusProjection;
import com.jake.messagesystem.dto.projection.UserIdUsernameInviterUserIdProjection;
import com.jake.messagesystem.entity.UserConnectionEntity;
import com.jake.messagesystem.entity.UserConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnectionEntity, UserConnectionId> {
    Optional<UserConnectionStatusProjection> findUserConnectionStatusByPartnerAUserIdAndPartnerBUserId(@NonNull Long partnerAUserId, @NonNull Long partnerBUserId);
    Optional<UserConnectionEntity> findByPartnerAUserIdAndPartnerBUserIdAndStatus(@NonNull Long partnerAUserId, @NonNull Long partnerBUserId, @NonNull UserConnectionStatus status);
    Optional<InviterUserIdProjection> findInviterUserIdByPartnerAUserIdAndPartnerBUserId(@NonNull Long partnerAUserId, @NonNull Long partnerBUserId);

    @Query(
            "SELECT u.partnerBUserId AS userId, userB.username AS username, u.inviterUserId AS inviterUserId "
                    + "FROM UserConnectionEntity u "
                    + "INNER JOIN UserEntity userB ON u.partnerBUserId = userB.userId "
                    + "WHERE u.partnerAUserId = :userId AND u.status = :status"
    )
    List<UserIdUsernameInviterUserIdProjection> findByPartnerAUserIdAndStatus(@Param("userId") Long userId, @Param("status") UserConnectionStatus status);

    @Query(
            "SELECT u.partnerAUserId AS userId, userA.username as username, u.inviterUserId AS inviterUserId "
                    + "FROM UserConnectionEntity u "
                    + "INNER JOIN UserEntity userA ON u.partnerAUserId = userA.userId "
                    + "WHERE u.partnerBUserId = :userId AND u.status = :status"
    )
    List<UserIdUsernameInviterUserIdProjection> findByPartnerBUserIdAndStatus(@Param("userId") Long userId, @Param("status") UserConnectionStatus status);
}
