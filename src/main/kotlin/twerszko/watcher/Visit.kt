package twerszko.watcher

import java.time.LocalDate
import java.time.LocalTime

data class Visit(
        val date: LocalDate,
        val time: LocalTime,
        val doctor: String,
        val location: String)