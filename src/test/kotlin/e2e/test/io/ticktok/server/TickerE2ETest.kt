package e2e.test.io.ticktok.server;

import e2e.test.io.ticktok.server.support.App.Companion.WEBSOCKET
import e2e.test.io.ticktok.server.support.Ticker
import org.junit.jupiter.api.Test;

class TickerE2ETest : CommonAppE2ETest() {

    override fun appProfile(): String {
        return WEBSOCKET
    }

    @Test
    fun receiveTickForSpecificTicker() {
        val ticker = Ticker.register(app.registerTicker("over-the-top"))
        val clock = app.registeredAClock("kuku", "@never", "over-the-top")
        app.tick(clock)
        ticker.receivedTickFor(clock)
    }

}
