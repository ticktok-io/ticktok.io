package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.AppDriver.Companion.HTTP
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.condition.DisabledIfSystemProperty

@DisabledIfSystemProperty(named = "scope", matches = "core")
@Tag("http-broker-tests")
class HttpBrokerE2ETest : CommonBrokerTest() {

    override fun appProfile(): String {
        return HTTP
    }
}