package e2e.test.io.ticktok.server.support

data class Clock(val id:String, val schedules: List<String>, val url: String) {
    var channel: ClockChannel? = null

    constructor(id: String, schedules: List<String>, url: String, channel: ClockChannel) : this(id, schedules, url) {
        this.channel = channel
    }

}

data class ClockChannel(val uri: String, val queue: String, val topic: String)
