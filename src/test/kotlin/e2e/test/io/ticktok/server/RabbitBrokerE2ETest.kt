package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App.Companion.RABBIT
import org.junit.jupiter.api.Tag

@Tag("rabbit-broker")
class RabbitBrokerE2ETest : CommonBrokerTest() {

    override fun appProfile(): String {
        return RABBIT
    }

}