package e2e.io.ticktok.server

import org.hamcrest.Matchers
import spock.lang.Unroll

import static e2e.io.ticktok.server.AppDriver.ClockMatcher.containsClock
import static org.hamcrest.Matchers.contains

class ApiSpec extends CommonSpec {

    @Override
    AppDriver app() {
        return AppDriver.instance('http')
    }

    @Override
    Client client() {
        return new Client()
    }

    def "test"() {
        given:
        def a = []

        expect:
        a contains("4")
    }

    def "register a new clock"() {
        given:
        app().registeredAClock("kuku", "every.5.seconds")

        expect:
        app().retrievedRegisteredClock("kuku", "every.5.seconds")
    }

    def "should be healthy"() {
        expect:
        app().isHealthy()
    }

    def "fail when token not provided"() {
        when:
        app().accessedWithoutAToken()

        then:
        app().retrieveAuthError()
    }

    def "retrieve configured clocks"() {
        given:
        def clock1 = app().registeredAClock("kuku6", "every.6.seconds")
        def clock2 = app().registeredAClock("popo10", "every.10.seconds")

        expect:
        def clocks = app().clocks()
        app().clocks(containsClock(clock1))
        clocks containsClock(clock2)
    }

    def "purge clocks with bo consumers"() {
        given:
        val clock = app().registeredAClock("purger", CLOCK_EXPR)
        sleep(1500)

        expect:
        app().clocks() not(containsClock(clock))
    }

    def "fail on in valid schedule"() {
        when:
        app().registeredAClock("kuku", "non-valid")

        then:
        app().retrievedUserError()
    }

    @Unroll("test repeated 2 times")
    def "handle concurrent clock requests"() {
        when:
        app().purge()
        sleep(500)
        invokeMultipleClockRequestsInParallel()

        then:
        app().allInteractionsSucceeded()
    }

    private def invokeMultipleClockRequestsInParallel() {
        val threads = (0..5).map {
            Thread { app().registeredAClock("popo", "every.1.seconds") }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
    }

    def "retrieve not found on non existing clock"() {
        when:
        app().fetchUnknownClock()

        then:
        app().retrievedNotFoundError()
    }

    def "stop ticks on clock pause"() {
        given:
        var clock = app().registeredAClock("to-be-disabled", "every.2.seconds")
        client().addClock(clock)

        when:
        clock = app().pauseClock(clock)

        then:
        client().receivesNoMoreTicks()
        assertThat(clock.status).isEqualTo("PAUSED")
        app().pauseActionIsNotAvailableFor(clock)
    }

    def "fail on unknown clock action"() {
        given:
        val clock = app().registeredAClock("stam", "every.1.seconds")

        when:
        app().invokeUnknownActionOn(clock)

        then:
        app().retrievedNotFoundError()
    }

    def "retrieve all clocks by name"() {
        given:
        app().registeredAClock("hop", "every.1.minutes")
        val clock = app().registeredAClock("lala", "every.1.minutes")

        expect:
        false //app().clocks(mapOf("name" to "lala"), containsOnly(clock));
    }

    def "send tick to an existing clock"() {
        given:
        val clock = app().registeredAClock("disabled", "@never")
        client().addClock(clock)

        when:
        app().tick(clock)

        then:
        client().receivedTicksFor(clock)
    }
}


