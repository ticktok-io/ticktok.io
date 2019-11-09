package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.condition.DisabledIfSystemProperty

@DisabledIfSystemProperty(named = "scope", matches = "core")
@Tag("http-broker-tests")
class HttpBrokerE2ETest : CommonAppE2ETest() {

    override fun app(): App {
        return App.instance("http")
    }
}