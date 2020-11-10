package e2e.test.io.ticktok.server.support


interface Client {
    fun addClock(clock: Clock)
    fun receivesNoMoreTicks()
    fun receivedTicksFor(clock: Clock)

}

class AnyClient : Client {



}

class HttpClient {

}