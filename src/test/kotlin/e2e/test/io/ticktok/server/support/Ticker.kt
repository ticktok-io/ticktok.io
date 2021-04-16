package e2e.test.io.ticktok.server.support

class Ticker {
    companion object {
        fun register(connectionDetails: TickerConnectionDetails): Ticker {
            return Ticker()
        }
    }


    fun receivedTickFor(clock: Clock) {

    }

}
