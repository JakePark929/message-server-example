package com.jake.messagesystem.repository;

import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.projection.InviterUserIdProjection;
import com.jake.messagesystem.dto.projection.UserConnectionStatusProjection;
import com.jake.messagesystem.entity.UserConnectionEntity;
import com.jake.messagesystem.entity.UserConnectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnectionEntity, UserConnectionId> {
    Optional<UserConnectionStatusProjection> findByPartnerAUserIdAndPartnerBUserId(@NonNull Long partnerAUserId, @NonNull Long partnerBUserId);
    Optional<UserConnectionEntity> findByPartnerAUserIdAndPartnerBUserIdAndStatus(@NonNull Long partnerAUserId, @NonNull Long partnerBUserId, @NonNull UserConnectionStatus status);
    Optional<InviterUserIdProjection> findInviterUserIdByPartnerAUserIdAndPartnerBUserId(@NonNull Long partnerAUserId, @NonNull Long partnerBUserId);
}
