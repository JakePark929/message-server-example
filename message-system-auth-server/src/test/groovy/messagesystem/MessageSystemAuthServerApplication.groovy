package messagesystem


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(classes = com.jake.messagesystem.MessageSystemAuthServerApplication)
@TestPropertySource(properties = ["SERVER_PORT=8080"])
class MessageSystemAuthServerApplication extends Specification {

    void contextLoads() {
        expect:
        true
    }

}
