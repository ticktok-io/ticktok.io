package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App.Companion.HTTP
import org.junit.jupiter.api.Tag

@Tag("http-broker")
class HttpBrokerE2ETest : CommonBrokerTest() {

    override fun appProfile(): String {
        return HTTP
    }
}