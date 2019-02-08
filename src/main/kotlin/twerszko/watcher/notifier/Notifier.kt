package twerszko.watcher.notifier

import twerszko.watcher.Visit

interface Notifier {
    fun notify(visits: List<Visit>)
}