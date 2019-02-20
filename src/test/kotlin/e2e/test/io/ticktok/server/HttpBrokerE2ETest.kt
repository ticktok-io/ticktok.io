package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Client
import e2e.test.io.ticktok.server.support.Client.CLOCK_EXPR
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfSystemProperty
import java.lang.Thread.sleep

@DisabledIfSystemProperty(named = "scope", matches = "core")
@Tag("ct")
class HttpBrokerE2ETest : CommonAppE2ETest() {

    override fun app(): App {
        return App.instance("http")
    }


    @Test
    fun failOnNonExistingQueue() {
        val clock = app().registeredAClock("http-404", CLOCK_EXPR)
        Client.stop()
        sleep(1000 * 60)
        Client.startListenTo(clock)
        Client.failToFindQueue()
    }


}