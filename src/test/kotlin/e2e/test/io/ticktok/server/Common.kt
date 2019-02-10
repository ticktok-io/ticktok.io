package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Client

fun verifyRetrieveScheduledMessage(app: App) {
    val clock = app.registeredAClock("kuku", Client.CLOCK_EXPR)
    Client.receivedTicksFor(clock)
}