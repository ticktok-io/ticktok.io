package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Client
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfSystemProperty

@DisabledIfSystemProperty(named = "scope", matches = "core")
@Tag("ct")
class HttpBrokerE2ETest : CommonAppE2ETest() {

    override fun app(): App {
        return App.instance("http")
    }


    @Test
    fun failOnNonExistingQueue() {

    }


}