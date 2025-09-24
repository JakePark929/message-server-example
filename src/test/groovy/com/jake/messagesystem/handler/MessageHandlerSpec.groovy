package com.jake.messagesystem.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.jake.messagesystem.MessageSystemApplication
import com.jake.messagesystem.dto.Message
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = MessageSystemApplication, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageHandlerSpec extends Specification {
    @LocalServerPort
    private int port

    private ObjectMapper objectMapper = new ObjectMapper()

    def "Group Chat Basic Test"() {
        given:
        def url = "ws://localhost:${port}/ws/v1/message"
        def (clientA, clientB, clientC) = [createClient(url), createClient(url), createClient(url)]

        when:
        clientA.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new Message("clientA", "안녕하세요. A 입니다."))))

        then:
        clientA.queue.isEmpty()
        clientB.queue.poll(1, TimeUnit.SECONDS).contains("clientA")
        clientC.queue.poll(1, TimeUnit.SECONDS).contains("clientA")

        cleanup:
        clientA.session?.close()
        clientB.session?.close()
        clientC.session?.close()
    }

    static def createClient(String url) {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(5)

        def client = new StandardWebSocketClient()
        def webSocketSession = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                blockingQueue.put(message.payload)
            }
        }, url).get()

        [queue: blockingQueue, session: webSocketSession]
    }
}
