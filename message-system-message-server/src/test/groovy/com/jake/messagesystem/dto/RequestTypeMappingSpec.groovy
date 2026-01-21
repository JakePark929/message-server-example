package com.jake.messagesystem.dto


import com.jake.messagesystem.dto.kafka.*
import com.jake.messagesystem.util.JsonUtil
import spock.lang.Specification

class RequestTypeMappingSpec extends Specification {
    JsonUtil jsonUtil = new JsonUtil()

    def "DTO 형식의 JSON 문자열을 해당 타입의 DTO 로 변환할 수 있다."() {
        given:
        String jsonBody = payload

        when:
        RecordInterface recordInterface = jsonUtil.fromJson(jsonBody, RecordInterface).get()

        then:
        recordInterface.getClass() == expectedClass
        validate(recordInterface)

        where:
        payload                                                                | expectedClass                    | validate
        '{"type": "FETCH_USER_INVITE_CODE_REQUEST"}'                           | FetchUserInviteCodeRequestRecord | { req -> (req as FetchUserInviteCodeRequestRecord).type() == 'FETCH_USER_INVITE_CODE_REQUEST' }
        '{"type": "FETCH_CONNECTIONS_REQUEST", "status": "ACCEPTED"}'          | FetchConnectionsRequestRecord    | { req -> (req as FetchConnectionsRequestRecord).status().name() == 'ACCEPTED' }
        '{"type": "INVITE_REQUEST", "userInviteCode": "TestInviteCode123"}'    | InviteRequestRecord              | { req -> (req as InviteRequestRecord).userInviteCode().code() == 'TestInviteCode123' }
        '{"type": "ACCEPT_REQUEST", "username": "testUser"}'                   | AcceptRequestRecord              | { req -> (req as AcceptRequestRecord).username() == 'testUser' }
        '{"type": "REJECT_REQUEST", "username": "testUser"}'                   | RejectRequestRecord              | { req -> (req as RejectRequestRecord).username() == 'testUser' }
        '{"type": "DISCONNECT_REQUEST", "username": "testUser"}'               | DisconnectRequestRecord          | { req -> (req as DisconnectRequestRecord).username() == 'testUser' }
        '{"type": "WRITE_MESSAGE", "channelId": 1, "content": "test message"}' | WriteMessageRecord               | { req -> (req as WriteMessageRecord).content() == 'test message' }
    }
}
