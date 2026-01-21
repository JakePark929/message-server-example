package com.jake.messagesystem.integration

import com.jake.messagesystem.MessageSystemMessageServerApplication
import com.jake.messagesystem.constants.KeyPrefix
import com.jake.messagesystem.constants.UserConnectionStatus
import com.jake.messagesystem.dto.UserId
import com.jake.messagesystem.entity.UserConnectionId
import com.jake.messagesystem.entity.UserEntity
import com.jake.messagesystem.repository.UserConnectionRepository
import com.jake.messagesystem.repository.UserRepository
import com.jake.messagesystem.service.CacheService
import com.jake.messagesystem.service.UserConnectionLimitService
import com.jake.messagesystem.service.UserConnectionService
import com.jake.messagesystem.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("default")
@SpringBootTest(classes = MessageSystemMessageServerApplication)
class UserConnectionServiceSpec extends Specification {
    @Autowired
    CacheService cacheService

    @Autowired
    UserService userService

    @Autowired
    UserConnectionService userConnectionService

    @Autowired
    UserConnectionLimitService userConnectionLimitService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserConnectionRepository userConnectionRepository

    def "연결 요청 수락은 연결 제한 수를 넘을 수 없다."() {
        given:
        userConnectionLimitService.setLimitConnections(10)
        (0..19).each { addUser("testuser${it}", "testpass${it}") }
        def userIdA = userService.getUserId("testuser0").get()
        def inviteCodeA = userService.getInviteCode(userIdA).get()

        (1..9).each {
            userConnectionService.invite(userService.getUserId("testuser${it}").get(), inviteCodeA)
            userConnectionService.accept(userIdA, "testuser${it}")
        }

        def inviteCodes = (10..19).collect() {
            userService.getInviteCode(userService.getUserId("testuser${it}").get()).get()
        }
        inviteCodes.each {
            userConnectionService.invite(userIdA, it)
        }

        def results = Collections.synchronizedList(new ArrayList<Optional<UserId>>())

        when:
        def threads = (10..19).collect { idx ->
            Thread.start {
                def userId = userService.getUserId("testuser${idx}")
                results << userConnectionService.accept(userId.get(), "testuser0").getFirst()
            }
        }
        threads*.join()

        then:
        results.count { it.isPresent() } == 1
    }

    def "연결 요청 카운트는 0보다 작을 수 없다."() {
        given:
        (0..10).each { addUser("testuser${it}", "testpass${it}") }
        def userIdA = userService.getUserId("testuser0").get()
        def inviteCodeA = userService.getInviteCode(userIdA).get()

        (1..10).each {
            userConnectionService.invite(userService.getUserId("testuser${it}").get(), inviteCodeA)
        }
        (1..5).each {
            userConnectionService.accept(userIdA, "testuser${it}")
        }

        def results = Collections.synchronizedList(new ArrayList<Boolean>())

        when:
        def threads = (1..10).collect { idx ->
            Thread.start {
                def userId = userService.getUserId("testuser${idx}")
                results << userConnectionService.disconnect(userId.get(), "testuser0").getFirst()
            }
        }
        threads*.join()

        then:
        results.count { it == true } == 5
        userService.getConnectionCount(userService.getUserId("testuser0").get()).get() == 0
    }

    def cleanup() {
        (0..19).each {
            userService.getUserId("testuser${it}").ifPresent { userId ->
                def userInviteCode = cacheService.get(cacheService.buildKey(KeyPrefix.USER_INVITE_CODE, userId.id().toString())).orElse("")
                cacheService.delete(List.of(
                        cacheService.buildKey(KeyPrefix.USER_ID, "testuser${it}"),
                        cacheService.buildKey(KeyPrefix.USERNAME, userId.id().toString()),
                        cacheService.buildKey(KeyPrefix.USER, userInviteCode),
                        cacheService.buildKey(KeyPrefix.USER_INVITE_CODE, userId.id().toString())
                ))

                userRepository.deleteById(userId.id())

                userConnectionRepository.findByPartnerAUserIdAndStatus(userId.id(), UserConnectionStatus.PENDING).each {
                    clearConnection(userId.id(), it.getUserId())
                }
                userConnectionRepository.findByPartnerBUserIdAndStatus(userId.id(), UserConnectionStatus.PENDING).each {
                    clearConnection(userId.id(), it.getUserId())
                }

                userConnectionRepository.findByPartnerAUserIdAndStatus(userId.id(), UserConnectionStatus.ACCEPTED).each {
                    clearConnection(userId.id(), it.getUserId())
                }
                userConnectionRepository.findByPartnerBUserIdAndStatus(userId.id(), UserConnectionStatus.ACCEPTED).each {
                    clearConnection(userId.id(), it.getUserId())
                }

                userConnectionRepository.findByPartnerAUserIdAndStatus(userId.id(), UserConnectionStatus.DISCONNECTED).each {
                    clearConnection(userId.id(), it.getUserId())
                }
                userConnectionRepository.findByPartnerBUserIdAndStatus(userId.id(), UserConnectionStatus.DISCONNECTED).each {
                    clearConnection(userId.id(), it.getUserId())
                }
            }
        }
    }

    def addUser(String username, String password) {
        final UserEntity userEntity = userRepository.save(new UserEntity(username, password))

        return new UserId(userEntity.getUserId())
    }

    def clearConnection(Long partnerA, Long partnerB) {
        def fist = Long.min(partnerA, partnerB)
        def second = Long.max(partnerA, partnerB)

        userConnectionRepository.deleteById(new UserConnectionId(fist, second))
        cacheService.delete(List.of(
                cacheService.buildKey(KeyPrefix.CONNECTION_STATUS, String.valueOf(fist), String.valueOf(second)),
                cacheService.buildKey(KeyPrefix.INVITER_USER_ID, String.valueOf(fist), String.valueOf(second))
        ))
    }
}
