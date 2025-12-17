package com.jake.messagesystem.service;

import com.jake.messagesystem.constants.UserConnectionStatus;
import com.jake.messagesystem.dto.InviteCode;
import com.jake.messagesystem.dto.User;
import com.jake.messagesystem.dto.UserId;
import com.jake.messagesystem.entity.UserConnectionEntity;
import com.jake.messagesystem.repository.UserConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final UserConnectionLimitService userConnectionLimitService;

    public UserConnectionService(UserService userService, UserConnectionRepository userConnectionRepository, UserConnectionLimitService userConnectionLimitService) {
        this.userService = userService;
        this.userConnectionRepository = userConnectionRepository;
        this.userConnectionLimitService = userConnectionLimitService;
    }

    @Transactional
    public Pair<Optional<UserId>, String> invite(UserId inviterUserId, InviteCode inviteCode) {
        final Optional<User> partner = userService.getUser(inviteCode);

        if (partner.isEmpty()) {
            log.info("Invalid invite code. {}, from {}", inviteCode, inviterUserId);

            return Pair.of(Optional.empty(), "Invalid invite code.");
        }

        final UserId partnerUserId = partner.get().userId();
        String partnerUsername = partner.get().username();

        if (partnerUserId.equals(inviterUserId)) {
            return Pair.of(Optional.empty(), "Can't self invite.");
        }

        UserConnectionStatus userConnectionStatus = getStatus(inviterUserId, partnerUserId);

        return switch (userConnectionStatus) {
            case NONE, DISCONNECTED -> {
                if (userService.getConnectionCount(inviterUserId).filter(count -> count >= userConnectionLimitService.getLimitConnections()).isPresent()) {
                    yield Pair.of(Optional.empty(), "Connection limit reached.");
                }

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

            case ACCEPTED -> Pair.of(Optional.of(partnerUserId), "Already connected with " + partnerUsername);

            case PENDING, REJECTED -> {
                log.info("{} invites {} but does not deliver the invitation request.", inviterUserId, partnerUsername);
                yield Pair.of(Optional.of(partnerUserId), "Already Invited to " + partnerUsername);
            }
        };
    }

    public Pair<Optional<UserId>, String> accept(UserId acceptorUserId, String inviterUsername) {
        final Optional<UserId> userId = userService.getUserId(inviterUsername);
        if (userId.isEmpty()) {
            return Pair.of(Optional.empty(), "Invalid username.");
        }

        UserId inviterUserId = userId.get();
        if (acceptorUserId.equals(inviterUserId)) {
            return Pair.of(Optional.empty(), "Can't self accept.");
        }

        if (getInviterUserId(acceptorUserId, inviterUserId).filter(invitationSenderUserId -> invitationSenderUserId.equals(inviterUserId)).isEmpty()) {
            return Pair.of(Optional.empty(), "Invalid username.");
        }

        final UserConnectionStatus userConnectionStatus = getStatus(inviterUserId, acceptorUserId);
        if (userConnectionStatus == UserConnectionStatus.ACCEPTED) {
            return Pair.of(Optional.empty(), "Already connected.");
        }

        if (userConnectionStatus != UserConnectionStatus.PENDING) {
            return Pair.of(Optional.empty(), "Accept failed.");
        }

        final Optional<String> acceptorUsername = userService.getUserName(acceptorUserId);
        if (acceptorUsername.isEmpty()) {
            log.error("Invalid userId. userid: {}", acceptorUserId);

            return Pair.of(Optional.empty(), "Accept failed.");
        }

        try {
            userConnectionLimitService.accept(acceptorUserId, inviterUserId);

            return Pair.of(Optional.of(inviterUserId), acceptorUsername.get());
        } catch (EntityNotFoundException e) {
            log.error("Accept failed. cause: {}", e.getMessage());

            return Pair.of(Optional.empty(), "Accept failed.");
        } catch (IllegalStateException e) {
            return Pair.of(Optional.empty(), e.getMessage());
        }
    }

    public Pair<Boolean, String> reject(UserId senderUserId, String inviterUsername) {
        return userService.getUserId(inviterUsername)
                .filter(inviterUserId -> !inviterUserId.equals(senderUserId))
                .filter(inviterUserId -> getInviterUserId(inviterUserId, senderUserId).filter(invitationSenderUserId -> invitationSenderUserId.equals(inviterUserId)).isPresent())
                .filter(inviterUserId -> getStatus(inviterUserId, senderUserId) == UserConnectionStatus.PENDING)
                .map(inviterUserId -> {
                    try {
                        setStatus(inviterUserId, senderUserId, UserConnectionStatus.REJECTED);

                        return Pair.of(true, inviterUsername);
                    } catch (Exception e) {
                        log.error("Set rejected failed. cause: {}", e.getMessage());

                        return Pair.of(false, "Reject failed.");
                    }
                }).orElse(Pair.of(false, "Reject failed."));
    }

    private Optional<UserId> getInviterUserId(UserId partnerAUserId, UserId partnerBUserId) {
        return userConnectionRepository.findInviterUserIdByPartnerAUserIdAndPartnerBUserId(
                Long.min(partnerAUserId.id(), partnerBUserId.id()),
                Long.max(partnerAUserId.id(), partnerBUserId.id())
        ).map(inviterUSerId -> new UserId(inviterUSerId.getInviterUserId()));
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
