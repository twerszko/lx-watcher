package twerszko.watcher.util

internal class Try<T>(private val what: () -> T) {
    private var times = 1
    private var pauseMs: Long = 0

    fun atMost(times: Int): Try<T> {
        this.times = times
        return this
    }

    fun waiting(milliseconds: Long): Try<T> {
        this.pauseMs = milliseconds
        return this
    }

    fun until(predicate: (T) -> Boolean): T {
        for (i in 0..times) {
            val content = what()
            if (predicate(content)) {
                return content
            }

            if (i < times - 1)
                Thread.sleep(pauseMs)
        }

        throw IllegalStateException()
    }
}