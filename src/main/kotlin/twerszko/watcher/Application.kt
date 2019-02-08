package twerszko.watcher

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Help.Visibility.NEVER
import picocli.CommandLine.Option
import twerszko.watcher.notifier.Notifier
import twerszko.watcher.notifier.StatefulNotifier
import twerszko.watcher.notifier.push.PushNotifier
import twerszko.watcher.notifier.sound.GilfoyleNotifier
import twerszko.watcher.notifier.text.LogNotifier
import twerszko.watcher.page.LoginPage
import twerszko.watcher.webdriver.WebDriverManager
import java.time.LocalDate

@Command(name = "java -jar lx-watcher.jar", versionProvider = VersionProvider::class, showDefaultValues = true)
class Application : Runnable {
    @Option(names = ["--login", "-l", "-u"], required = true, showDefaultValue = NEVER)
    private var login: String = ""

    @Option(names = ["--password", "-p"], required = true, showDefaultValue = NEVER)
    private var password: String = ""

    @Option(names = ["--visit-type", "-t"], required = true, showDefaultValue = NEVER,
            description = ["Valid values: \${COMPLETION-CANDIDATES}"])
    private var visitType: VisitType = VisitType.INTERNIST

    @Option(names = ["--date-from"], description = ["Format: YYYY-MM-DD"])
    private var dateFrom: LocalDate? = null

    @Option(names = ["--date-to"], description = ["Format: YYYY-MM-DD"])
    private var dateTo: LocalDate? = null

    @Option(names = ["--search-interval-ms"])
    private var searchIntervalMs: Long = 60_000

    @Option(names = ["--search-tires"])
    private var searchTries: Int = 120

    @Option(names = ["--chrome-driver-version"], description = ["Chrome Web Driver version to be downloaded when executable is not present"])
    private var chromeDriverVersion: String = "2.46"

    @Option(names = ["--sound-disabled"], description = ["Disables sound notifier"])
    private var soundDisabled: Boolean = false

    @Option(names = ["--notify-run-channel"], showDefaultValue = NEVER, description = [
        "Notify.run channel name. Enables push message notifier when set.",
        "See https://notify.run/ for details."])
    private var notifyRunChannel: String = ""

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["prints this help and exits"])
    private var helpRequested: Boolean = false

    @Option(names = ["-V", "--version"], versionHelp = true, description = ["prints version information and exits"])
    private var versionRequested: Boolean = false

    override fun run() {
        if (helpRequested) {
            CommandLine(this).usage(System.err)
            return
        }

        if (versionRequested) {
            CommandLine(this).printVersionHelp(System.out)
            return
        }

        val driver = WebDriverManager(chromeDriverVersion).init()
        try {
            LoginPage(driver, "https://portalpacjenta.luxmed.pl")
                    .login(login, password)
                    .search(searchOptions(), notifier())
        } finally {
            driver.quit()
        }
    }

    private fun notifier(): Notifier {
        val notifiers: List<Notifier> = listOfNotNull(
                soundDisabled.takeUnless { it }?.let { GilfoyleNotifier() },
                LogNotifier(),
                notifyRunChannel.takeIf { it.isNotBlank() }?.let { PushNotifier.create("https://notify.run/$it") }
        )

        return object : StatefulNotifier() {
            override fun doNotify(visits: List<Visit>) {
                notifiers.forEach { it.notify(visits) }
            }

        }
    }

    private fun searchOptions() = SearchOptions(
            visitType = visitType,
            dateFrom = dateFrom,
            dateTo = dateTo,
            searchIntervalMs = searchIntervalMs,
            searchTries = searchTries
    )
}

fun main(args: Array<String>) {
    CommandLine.run(Application(), System.err, *args)
}