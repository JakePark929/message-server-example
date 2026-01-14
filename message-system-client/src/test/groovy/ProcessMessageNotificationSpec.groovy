import com.jake.messagesystem.dto.*
import com.jake.messagesystem.dto.websocket.inbound.MessageNotification
import com.jake.messagesystem.dto.websocket.outbound.FetchMessagesRequest
import com.jake.messagesystem.dto.websocket.outbound.ReadMessageAck
import com.jake.messagesystem.service.MessageService
import com.jake.messagesystem.service.TerminalService
import com.jake.messagesystem.service.UserService
import com.jake.messagesystem.service.WebSocketService
import spock.lang.Specification

class ProcessMessageNotificationSpec extends Specification {
    TerminalService terminalService = Mock()
    WebSocketService webSocketService = Mock()
    UserService userService
    MessageService messageService

    def setup() {
        userService = new UserService()
        messageService = new MessageService(terminalService, userService)
        messageService.setWebSocketService(webSocketService)
    }

    def '채널에 입장하지 않은 상태에서 받은 메세지는 무시한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToLobby()

        when:
        messageService.receiveMessage(new MessageNotification(channelId, new MessageSeqId(100), 'bob', 'hello'))

        then:
        0 * terminalService.printMessage()
        0 * webSocketService.sendMessage(_)
    }

    def 'UserService 는 참여 중인 채널에서 마지막으로 받은 메세지의 MessageSeqId 를 가지고 있다.'() {
        given:
        def channelId = new ChannelId(5)
        def messageSeqId = new MessageSeqId(100)
        userService.moveToChannel(channelId)

        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, 'bob', 'hello'))

        then:
        userService.getLastReadMessageSeqId() == messageSeqId
    }

    def '새로 받은 메세지의 MessageSeqId가 이전에 받은 메세지의 MessageSeqId 보다 정확히 1이 크다면, ack 를 보내야 한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(100))
        def messageSeqId = new MessageSeqId(101)
        def expectMessageReadAckRequest = new ReadMessageAck(channelId, messageSeqId)

        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, "bob", "hello."))

        then:
        1 * webSocketService.sendMessage(expectMessageReadAckRequest)
    }

    def '새로 받은 메세지의 MessageSeqId 가 이전에 받은 메세지의 MessageSeqId 보다 2이상 크다면 누락된 메세지를 요청해야 한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(100))
        def messageSeqId = new MessageSeqId(102)
        def expectFetchMessagesRequest = null

        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, 'bob', 'hello.'))
        def fetchMessagesRequest = new FetchMessagesRequest(channelId, new MessageSeqId(userService.getLastReadMessageSeqId().id() + 1), new MessageSeqId((messageSeqId.id() - 1)))
        sleep(200)

        then:
        1 * webSocketService.sendMessage(_) >> { List args -> expectFetchMessagesRequest = args[0] }
        expectFetchMessagesRequest == fetchMessagesRequest

        and:
        userService.peekMessage().messageSeqId() == messageSeqId
    }

    def '새로 받은 메세지의 MessageSeqId 가 이전에 받은 메세지의 MessageSeqId와 같거나 작으면 무시한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(100))
        def messageSeqId = new MessageSeqId(100)

        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, 'bob', 'hello.'))

        then:
        1 * terminalService.printSystemMessage(_)
        0 * terminalService.printMessage(_)
        0 * webSocketService.sendMessage(_)
    }

    def '버퍼에 저장된 메세지가 있으면 처리한다.'() {
        given:
        def channelId = new ChannelId(5)
        def user = new User(new UserId(3), 'bob')
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(8))
        userService.addMessage(new Message(channelId, new MessageSeqId(10), user.username(), '10 hi'))
        userService.addMessage(new Message(channelId, new MessageSeqId(11), user.username(), '11 hello'))
        userService.addMessage(new Message(channelId, new MessageSeqId(13), user.username(), '13 good.'))

        when:
        messageService.receiveMessage(new MessageNotification(channelId, new MessageSeqId(9), 'bob', '9 hello.'))

        then:
        1 * terminalService.printMessage(_, '9 hello.')
        1 * terminalService.printMessage(_, '10 hi')
        1 * terminalService.printMessage(_, '11 hello')
    }
}
