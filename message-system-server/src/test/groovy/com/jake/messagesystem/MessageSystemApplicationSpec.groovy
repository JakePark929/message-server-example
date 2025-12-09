package com.jake.messagesystem;

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification;

@SpringBootTest(classes = MessageSystemApplication)
@ActiveProfiles("test")
class MessageSystemApplicationSpec extends Specification {

    void contextLoads() {
        expect:
        true
    }

}
