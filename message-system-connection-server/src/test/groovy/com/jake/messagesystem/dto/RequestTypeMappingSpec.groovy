package com.jake.messagesystem.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.jake.messagesystem.dto.websocket.inbound.*
import com.jake.messagesystem.util.JsonUtil
import spock.lang.Specification

class RequestTypeMappingSpec extends Specification {
    JsonUtil jsonUtil = new JsonUtil(new ObjectMapper())

    def "DTO 형식의 JSON 문자열을 해당 타입의 DTO 로 변환할 수 있다."() {
        given:
        String jsonBody = payload

        when:
        BaseRequest request = jsonUtil.fromJson(jsonBody, BaseRequest).get()

        then:
        request.getClass() == expectedClass
        validate(request)

        where:
        payload                                                                | expectedClass              | validate
        '{"type": "FETCH_USER_INVITE_CODE_REQUEST"}'                           | FetchUserInviteCodeRequest | { req -> (req as FetchUserInviteCodeRequest).type == 'FETCH_USER_INVITE_CODE_REQUEST' }
        '{"type": "FETCH_CONNECTIONS_REQUEST", "status": "ACCEPTED"}'          | FetchConnectionsRequest    | { req -> (req as FetchConnectionsRequest).status.name() == 'ACCEPTED' }
        '{"type": "INVITE_REQUEST", "userInviteCode": "TestInviteCode123"}'    | InviteRequest              | { req -> (req as InviteRequest).userInviteCode.code() == 'TestInviteCode123' }
        '{"type": "ACCEPT_REQUEST", "username": "testUser"}'                   | AcceptRequest              | { req -> (req as AcceptRequest).username == 'testUser' }
        '{"type": "REJECT_REQUEST", "username": "testUser"}'                   | RejectRequest              | { req -> (req as RejectRequest).username == 'testUser' }
        '{"type": "DISCONNECT_REQUEST", "username": "testUser"}'               | DisconnectRequest          | { req -> (req as DisconnectRequest).username == 'testUser' }
        '{"type": "WRITE_MESSAGE", "channelId": 1, "content": "test message"}' | WriteMessage               | { req -> (req as WriteMessage).content == 'test message' }
        '{"type": "KEEP_ALIVE"}'                                               | KeepAlive                  | { req -> (req as KeepAlive).getType() == 'KEEP_ALIVE' }
    }
}
