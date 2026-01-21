package com.jake.messagesystem

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = MessageSystemMessageServerApplication)
class MessageSystemMessageServerApplicationSpec extends Specification {

    void contextLoads() {
        expect:
        true
    }

}
