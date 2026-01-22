package com.jake.messagesystem

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(classes = MessageSystemApplication)
@TestPropertySource(properties = ["SERVER_PORT=8090", "server.id=test"])
class MessageSystemApplicationSpec extends Specification {

    void contextLoads() {
        expect:
        true
    }

}
