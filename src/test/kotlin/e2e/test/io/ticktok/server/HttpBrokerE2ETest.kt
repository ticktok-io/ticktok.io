package e2e.test.io.ticktok.server

import e2e.test.io.ticktok.server.support.App
import e2e.test.io.ticktok.server.support.Client
import org.junit.jupiter.api.Test

class HttpBrokerE2ETest : CommonAppE2ETest() {

    override fun app(): App {
        return App.instance("http")
    }


    @Test
    fun failOnNonExistingQueue() {

    }

//    @Test
//    fun retrieveScheduledMessage() {
//        val clock = app().registeredAClock("kuku", "every.30.seconds")
//        Client.receivedTicksFor(clock)
//        Client.receivesNoMoreTicksFor(clock)
//        Client.receivedTicksFor(app().registeredAClock("popov", "every.40.seconds"))
//    }

}