package twerszko.watcher.notifier.push

import feign.Feign
import org.slf4j.LoggerFactory
import twerszko.watcher.Visit
import twerszko.watcher.notifier.Notifier

class PushNotifier private constructor (private val pushClient: PushClient) : Notifier {
    private val log = LoggerFactory.getLogger(PushNotifier::class.java)

    override fun notify(visits: List<Visit>) {
        val range = when(visits.size) {
            1 -> {
                val visit = visits.first()
                "${visit.date} ${visit.time} ${visit.doctor} - ${visit.location}"
            }
            else -> {
                val first = visits.first()
                val last = visits.last()
                "${first.date} ${first.time} ${first.doctor} ... ${last.date} ${last.time} ${last.doctor}"
            }
        }
        val message = "New visits available (${visits.size}): $range"
        log.info("Sending push message '$message'")
        pushClient.push(message)
    }

    companion object {
        @JvmStatic
        fun create(url: String): PushNotifier{
            val client = Feign.builder().target(PushClient::class.java, url)
            return PushNotifier(client)
        }
    }
}