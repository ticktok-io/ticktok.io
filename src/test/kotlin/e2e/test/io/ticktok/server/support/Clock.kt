package e2e.test.io.ticktok.server.support

data class Clock(val schedule:String, val url:String, var channel: ClockChannel)

data class ClockChannel(val uri: String, val exchange: String, val topic: String)
