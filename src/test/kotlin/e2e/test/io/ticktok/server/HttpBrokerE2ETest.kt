package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.HttpTickConsumer
import e2e.test.io.ticktok.server.support.TickConsumer
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.condition.DisabledIfSystemProperty

@DisabledIfSystemProperty(named = "scope", matches = "core")
@Tag("http-broker-tests")
class HttpBrokerE2ETest : CommonAppE2ETest() {

    override fun appProfile(): String {
        return App.HTTP
    }

    override fun tickConsumer(): TickConsumer {
        return HttpTickConsumer()
    }
}