package twerszko.watcher.notifier

import twerszko.watcher.Visit

abstract class StatefulNotifier : Notifier {
    private var previousState = emptySet<Visit>()

    override fun notify(visits: List<Visit>) {
        val newVisits = visits.asSequence().minus(previousState).toList()
        previousState = visits.toSet()

        if(newVisits.isNotEmpty())
            doNotify(newVisits)
    }

    abstract fun doNotify(visits: List<Visit>)
}