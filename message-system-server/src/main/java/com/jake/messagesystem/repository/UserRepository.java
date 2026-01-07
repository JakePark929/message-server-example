package com.jake.messagesystem.repository;

import com.jake.messagesystem.dto.projection.CountProjection;
import com.jake.messagesystem.dto.projection.InviteCodeProjection;
import com.jake.messagesystem.dto.projection.UserIdProjection;
import com.jake.messagesystem.dto.projection.UsernameProjection;
import com.jake.messagesystem.entity.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserIdProjection> findUserIdByUsername(@NonNull String username);
    List<UserIdProjection> findByUsernameIn(@NonNull Collection<String> usernames);
    Optional<UsernameProjection> findByUserId(@NonNull Long userId);
    Optional<UserEntity> findByInviteCode(@NonNull String inviteCode);
    Optional<InviteCodeProjection> findInviteCodeByUserId(@NonNull Long userId);
    Optional<CountProjection> findCountByUserId(@NonNull Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserEntity> findForUpdateByUserId(@NonNull Long userId);
}
