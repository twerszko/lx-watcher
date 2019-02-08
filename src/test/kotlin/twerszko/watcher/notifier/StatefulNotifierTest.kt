package twerszko.watcher.notifier

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import twerszko.watcher.Visit
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter.ofPattern


internal class StatefulNotifierTest {

    private var notifications: MutableList<List<Visit>> = mutableListOf()

    private var underTest: Notifier = object : StatefulNotifier() {
        override fun doNotify(visits: List<Visit>) {
            notifications.add(visits)
        }
    }

    @Test
    fun `should not notify when no visits`() {
        underTest.notify(emptyList())
        underTest.notify(emptyList())
        underTest.notify(emptyList())

        assertThat(notifications).isEmpty()
    }

    @Test
    fun `should notify when new visit available`() {
        underTest.notify(emptyList())
        underTest.notify(listOf(Visit(date("03-02-2019"), time("12:00"), "X", "Y")))
        underTest.notify(emptyList())

        assertThat(notifications).containsExactly(listOf(Visit(date("03-02-2019"), time("12:00"), "X", "Y")))
    }

    @Test
    fun `should not notify again when the same visit is available`() {
        underTest.notify(emptyList())
        underTest.notify(listOf(Visit(date("03-02-2019"), time("12:00"), "X", "Y")))
        underTest.notify(listOf(Visit(date("03-02-2019"), time("12:00"), "X", "Y")))
        underTest.notify(emptyList())

        assertThat(notifications).containsExactly(listOf(Visit(date("03-02-2019"), time("12:00"), "X", "Y")))
    }

    @Test
    fun `should only notify differences`() {
        underTest.notify(emptyList())
        underTest.notify(listOf(
                Visit(date("03-02-2019"), time("12:00"), "X", "Y")))
        underTest.notify(listOf(
                Visit(date("03-02-2019"), time("12:00"), "X", "Y"),
                Visit(date("03-02-2019"), time("13:00"), "XX", "YY")))
        underTest.notify(emptyList())

        assertThat(notifications).containsExactly(
                listOf(Visit(date("03-02-2019"), time("12:00"), "X", "Y")),
                listOf(Visit(date("03-02-2019"), time("13:00"), "XX", "YY")))
    }

    private fun date(value: String) = LocalDate.parse(value, ofPattern("dd-MM-yyyy"))
    private fun time(value: String) = LocalTime.parse(value, ofPattern("HH:mm"))
}