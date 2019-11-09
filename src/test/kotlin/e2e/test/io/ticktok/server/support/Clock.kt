package e2e.test.io.ticktok.server.support

data class Clock(
        val id: String,
        val name: String,
        val schedule: String,
        val url: String?,
        val status: String,
        val links: List<Map<String, *>>?) {

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
                links: List<Map<String, *>>,
                channel: ClockChannel) : this(id, name, schedule, url, status, links) {
        this.channel = channel
    }

    fun linkFor(action: String): String? {
        val linksForAction = links?.filter { l -> l["rel"] == action }
        if(linksForAction == null || linksForAction.isEmpty()) {
            return null
        }
        return linksForAction.single()["href"] as String?

    }
}

data class ClockChannel(
        val type: String,
        val details: Map<String, String>)

