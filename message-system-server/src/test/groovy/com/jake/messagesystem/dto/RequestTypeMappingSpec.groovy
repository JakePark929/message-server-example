package com.jake.messagesystem.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.jake.messagesystem.dto.websocket.inbound.BaseRequest
import com.jake.messagesystem.dto.websocket.inbound.InviteRequest
import com.jake.messagesystem.dto.websocket.inbound.KeepAliveRequest
import com.jake.messagesystem.dto.websocket.inbound.WriteMessageRequest
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
        payload | expectedClass | validate
        '{"type": "INVITE_REQUEST", "userInviteCode": "TestInviteCode123"}' | InviteRequest | {req -> (req as InviteRequest).userInviteCode.code() == 'TestInviteCode123'}
        '{"type": "WRITE_MESSAGE", "username": "TestUser", "content": "test message"}' | WriteMessageRequest | { req -> (req as WriteMessageRequest).content == 'test message'}
        '{"type": "KEEP_ALIVE"}' | KeepAliveRequest | { req -> (req as KeepAliveRequest).getType() == 'KEEP_ALIVE'}
    }
}
