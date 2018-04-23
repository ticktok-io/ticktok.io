package e2e.test.io.ticktok.server.support

data class Clock(val schedule: String, val url: String) {
    var channel: ClockChannel? = null

    constructor(schedule: String, url: String, channel: ClockChannel) : this(schedule, url) {
        this.channel = channel
    }

}

data class ClockChannel(val uri: String, val exchange: String, val topic: String)
