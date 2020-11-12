package e2e.io.ticktok.server

import spock.lang.Specification

abstract class CommonSpec extends Specification {

    def cleanup() {
        app().reset()
        client().stop()
    }

    abstract AppDriver app()

    abstract Client client()

    def cleanupSpec() {
        app().purge()
        app().shutdown()
    }
}
