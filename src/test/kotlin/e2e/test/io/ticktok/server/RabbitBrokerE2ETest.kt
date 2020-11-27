package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.AppDriver.Companion.RABBIT
import e2e.test.io.ticktok.server.support.RabbitTickConsumer
import e2e.test.io.ticktok.server.support.TickConsumer

class RabbitBrokerE2ETest : CommonBrokerTest() {

    override fun appProfile(): String {
        return RABBIT
    }

}