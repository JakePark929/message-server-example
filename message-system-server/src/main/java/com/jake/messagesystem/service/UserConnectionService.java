package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.User;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.entity.UserConnectionEntity;
import com.jake.messagesystem.repository.UserConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserConnectionService {
    private final Logger log = LoggerFactory.getLogger(UserConnectionService.class);

    private final UserService userService;
    private final UserConnectionRepository userConnectionRepository;

    public UserConnectionService(UserService userService, UserConnectionRepository userConnectionRepository) {
        this.userService = userService;
        this.userConnectionRepository = userConnectionRepository;
    }

    @Transactional
    public Pair<Optional<UserId>, String> invite(UserId inviterUserId, InviteCode inviteCode) {
        final Optional<User> partner = userService.getUser(inviteCode);

        if (partner.isEmpty()) {
            log.info("Invalid invite code. {}, from {}", inviteCode, inviterUserId);

            return Pair.of(Optional.empty(), "Invalid invite code");
        }

        final UserId partnerUserId = partner.get().userId();
        String partnerUsername = partner.get().username();

        if (partnerUserId.equals(inviterUserId)) {
            return Pair.of(Optional.empty(), "Can't self invite.");
        }

        UserConnectionStatus userConnectionStatus = getStatus(inviterUserId, partnerUserId);

        return switch (userConnectionStatus) {
            case NONE, DISCONNECTED -> {
                final Optional<String> inviterUsername = userService.getUserName(inviterUserId);

                if (inviterUsername.isEmpty()) {
                    log.warn("InviteRequest failed.");
                    yield Pair.of(Optional.empty(), "InviteRequest failed.");
                }

                try {
                    setStatus(inviterUserId, partnerUserId, UserConnectionStatus.PENDING);

                    yield Pair.of(Optional.of(partnerUserId), inviterUsername.get());
                } catch (Exception e) {
                    log.error("Set pending failed. cause: {}", e.getMessage());

                    yield Pair.of(Optional.empty(), "InviteRequest failed.");
                }
            }

            case ACCEPTED -> Pair.of(Optional.empty(), "Already connected with " + partnerUsername);

            case PENDING, REJECTED -> {
                log.info("{} invites {} but does not deliver the invitation request.", inviterUserId, partnerUsername);
                yield Pair.of(Optional.empty(), "Already connected with " + partnerUsername);
            }
        };
    }

    private UserConnectionStatus getStatus(UserId inviterUserId, UserId partnerUserId) {
        return userConnectionRepository.findByPartnerAUserIdAndPartnerBUserId(Long.min(inviterUserId.id(), partnerUserId.id()), Long.max(inviterUserId.id(), partnerUserId.id()))
                .map(status -> UserConnectionStatus.valueOf(status.getStatus()))
                .orElse(UserConnectionStatus.NONE);
    }


    private void setStatus(UserId inviterUserId, UserId partnerUserId, UserConnectionStatus userConnectionStatus) {
        if (userConnectionStatus.equals(UserConnectionStatus.ACCEPTED)) {
            throw new IllegalArgumentException("Can't set to accepted.");
        }

        userConnectionRepository.save(
                new UserConnectionEntity(
                        Long.min(inviterUserId.id(), partnerUserId.id()),
                        Long.max(inviterUserId.id(), partnerUserId.id()),
                        userConnectionStatus,
                        inviterUserId.id()
                )
        );
    }
}
