package e2e.test.io.ticktok.server.support

data class Clock(
        val id: String,
        val name: String,
        val schedule: String,
        val url: String?,
        val status: String,
        val _links: Map<String, Any>?) {


    companion object {
        const val ACTIVE = "ACTIVE"
    }

    // This is here so it wont be included in comparision
    var channel: ClockChannel? = null

    constructor(id: String,
                name: String,
                schedule: String,
                url: String,
                status: String,
                _links: Map<String, Any>,
                channel: ClockChannel) : this(id, name, schedule, url, status, _links) {
        this.channel = channel
    }
}

data class ClockChannel(
        val type: String,
        val details: Map<String, String>)

