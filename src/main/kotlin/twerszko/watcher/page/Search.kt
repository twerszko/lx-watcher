package twerszko.watcher.page

import cyclops.async.LazyReact
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import twerszko.watcher.SearchOptions
import twerszko.watcher.Visit
import twerszko.watcher.VisitType
import twerszko.watcher.notifier.Notifier
import twerszko.watcher.util.PageUtils.click
import twerszko.watcher.util.PageUtils.clickIfPossible
import twerszko.watcher.util.PageUtils.runJs
import twerszko.watcher.util.PageUtils.setValue
import twerszko.watcher.util.Try
import java.lang.Math.max
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.stream.Collectors

class Search internal constructor(private val webDriver: WebDriver, private val baseUrl: String) {
    private val log = LoggerFactory.getLogger(Search::class.java)
    private val numberOfThreads = max(2, (Runtime.getRuntime().availableProcessors() - 1))
    private val executor = newFixedThreadPool(numberOfThreads)

    private val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    init {
        log.info("$numberOfThreads threads will be used for results processing")
    }

    fun search(options: SearchOptions, notifier: Notifier) {
        captureLink()
        webDriver.get(createUrl(baseUrl, options.visitType))
        waitUntilSpinnerDisappears()

        options.dateFrom?.let { setValue(webDriver, "FromDate", it.format(dateFormat))}
        options.dateTo?.let { setValue(webDriver, "ToDate", it.format(dateFormat))}

        waitUntilSpinnerDisappears()

        for (i in 0 until options.searchTries) {
            log.info("Searching. Try ${i + 1}/${options.searchTries}")
            click { searchButton() }
            waitUntilSpinnerDisappears()
            waitUntilDataAvailable()
            log.info("Processing results")
            val results = processResults()
            log.info("Calling notifiers")
            notifier.notify(results)
            log.info("Sleeping for {}ms", options.searchIntervalMs)
            Thread.sleep(options.searchIntervalMs)
        }

    }

    private fun waitUntilDataAvailable() {
        Try { webDriver.findElements(By.id("data-container")) }
                .atMost(5)
                .waiting(2_000)
                .until { elements -> elements.size > 0 }
    }

    private fun processResults(): List<Visit> {
        val days = days()
        return days.flatMap { createDayVisits(it) }
    }

    private fun createDayVisits(dayTitle: WebElement): List<Visit> {
        val date = visitDate(dayTitle)
        return parentContent(dayTitle)
                .flatMap { createDayVisits(date, it) }
    }

    private fun createDayVisits(date: LocalDate, dayContent: WebElement): List<Visit> {
        return LazyReact(numberOfThreads, executor)
                .async()
                .from(visitRows(dayContent))
                .map { day -> day?.let { createVisit(date, day) } }
                .filter { it != null }
                .map { it!! }
                .run(Collectors.toList())
    }

    private fun createVisit(date: LocalDate, row: WebElement): Visit? {
        val columns = visitColumns(row)
        val time = visitTime(columns[0]) ?: return null
        val details = columns[1].findElements(By.tagName("div"))
        val doctor = details[0].getAttribute("innerHTML").trim()
        val location = details[2].getAttribute("innerHTML").trim()
        return Visit(date, time, doctor, location)
    }

    private fun days(): List<WebElement> {
        return webDriver.findElement(By.id("data-container")).findElements(By.xpath(".//div[@class='title']"))
    }

    private fun parentContent(dayTitle: WebElement): List<WebElement> {
        return dayTitle.findElement(By.xpath(".."))
                .findElements(By.xpath(".//div[@class='content']"))
    }

    private fun visitRows(dayContent: WebElement) = dayContent.findElements(By.xpath(".//tr"))

    private fun visitColumns(row: WebElement) = row.findElements(By.tagName("td"))

    private fun visitDate(dayTitle: WebElement) =
            "\\d{2}-\\d{2}-\\d{4}".toRegex().find(dayTitle.getAttribute("outerHTML"))!!.value
                    .let { LocalDate.parse(it, dateFormat) }

    private fun visitTime(visitTimeColumn: WebElement): LocalTime? {
        return visitTimeColumn.getAttribute("data-sort")?.let { LocalTime.parse(it, timeFormat) }
    }

    private fun searchButton() = searchForm().findElement(By.xpath(".//input[@id='reservationSearchSubmitButton']"))

    private fun searchForm(): WebElement {
        return webDriver.findElement(By.id("advancedResevation"))
    }

    private fun captureLink() {
        // Prevents automating logout on page unload
        runJs(webDriver, "captureLink()")
    }

    private fun createUrl(baseUrl: String, visitType: VisitType): String {
        return "$baseUrl$URL_SEARCH${visitType.actionId}"
    }

    private fun waitUntilSpinnerDisappears() {
        Try { searchForm() }
                .atMost(5)
                .waiting(2_000)
                .until { searchForm -> clickIfPossible(webDriver, searchForm) }
    }

    companion object {
        const val URL_SEARCH = "/PatientPortal/Reservations/Reservation/Coordination?activityId="
    }
}