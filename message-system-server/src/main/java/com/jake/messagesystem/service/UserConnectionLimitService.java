package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.entity.UserConnectionEntity;
import com.jake.messagesystem.entity.UserEntity;
import com.jake.messagesystem.repository.UserConnectionRepository;
import com.jake.messagesystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
public class UserConnectionLimitService {
    private final UserRepository userRepository;
    private final UserConnectionRepository userConnectionRepository;

    private int limitConnections = 1_000;

    public UserConnectionLimitService(UserRepository userRepository, UserConnectionRepository userConnectionRepository) {
        this.userRepository = userRepository;
        this.userConnectionRepository = userConnectionRepository;
    }

    public int getLimitConnections() {
        return limitConnections;
    }

    public void setLimitConnections(int limitConnections) {
        this.limitConnections = limitConnections;
    }

    @Transactional
    public void accept(UserId acceptorUserId, UserId inviterUserid) {
        Long firstUserId = Long.min(acceptorUserId.id(), inviterUserid.id());
        Long secondUserId = Long.max(acceptorUserId.id(), inviterUserid.id());

        final UserEntity firstUserEntity = userRepository.findForUpdateByUserId(firstUserId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid userId: " + firstUserId));
        final UserEntity secondUserEntity = userRepository.findForUpdateByUserId(secondUserId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid userId: " + secondUserId));

        final UserConnectionEntity userConnectionEntity = userConnectionRepository.findByPartnerAUserIdAndPartnerBUserIdAndStatus(firstUserId, secondUserId, UserConnectionStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Invalid status."));


        Function<Long, String> getErrorMessage = userId -> userId.equals(acceptorUserId.id()) ? "Connection limit reached." : "Connection limit reached by other user.";

        int firstConnectionCount = firstUserEntity.getConnectionCount();
        if (firstConnectionCount >= limitConnections) {
            throw new IllegalStateException(getErrorMessage.apply(firstUserId));
        }

        int secondConnectionCount = secondUserEntity.getConnectionCount();
        if (secondConnectionCount >= limitConnections) {
            throw new IllegalStateException(getErrorMessage.apply(secondUserId));
        }

        firstUserEntity.setConnectionCount(firstConnectionCount + 1);
        secondUserEntity.setConnectionCount(secondConnectionCount + 1);
        userConnectionEntity.setStatus(UserConnectionStatus.ACCEPTED);
    }
}
