package twerszko.watcher.notifier.text

import org.slf4j.LoggerFactory
import twerszko.watcher.Visit
import twerszko.watcher.notifier.Notifier

class LogNotifier : Notifier {
    private val log = LoggerFactory.getLogger(LogNotifier::class.java)

    override fun notify(visits: List<Visit>) {
        log.info("New visits available (${visits.size}):${
        visits.asSequence()
                .map { "${it.date} ${it.time} ${it.doctor} ${it.location}" }
                .fold("") { result, current -> "$result\n$current" }
        }")
    }
}