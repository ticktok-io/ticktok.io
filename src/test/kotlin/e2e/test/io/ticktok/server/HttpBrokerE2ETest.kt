package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App

class HttpBrokerE2ETest : CommonAppE2ETest() {

    override fun app(): App {
        return App.instance("http")
    }

    fun retrieveScheduledMessage() {
        verifyRetrieveScheduledMessage(app)
    }

}