package twerszko.watcher

import java.time.LocalDate

data class SearchOptions(
        val visitType: VisitType,
        val dateFrom: LocalDate?,
        val dateTo: LocalDate?,
        val searchIntervalMs: Long,
        val searchTries: Int
)