package e2e.test.io.ticktok.server.support

data class Clock(val id: String, val name: String, val schedule: String, val url: String?, val status: String) {

    companion object {
        const val ACTIVE = "ACTIVE"
    }

    var channel: ClockChannel? = null

    constructor(id: String, name: String, schedule: String, url: String, status: String, channel: ClockChannel) : this(id, name, schedule, url, status) {
        this.channel = channel
    }
}

data class ClockChannel(val uri: String, val queue: String, val topic: String)
