package twerszko.watcher.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class WebDriverManager(private val version: String) {
    private val log = LoggerFactory.getLogger(WebDriverManager::class.java)

    fun init(): WebDriver {
        val webDriver = WebDriverDownloader.create(version).downloadIfNeeded()
        log.info("Setting Chrome Web Driver path '${webDriver.absolutePath}'")
        System.setProperty("webdriver.chrome.driver", webDriver.absolutePath)
        return ChromeDriver()
    }
}

private abstract class WebDriverDownloader(val version: String, val executableName: String, val packageName: String) {
    private val url: String = "https://chromedriver.storage.googleapis.com/$version/"
    private val log = LoggerFactory.getLogger(WebDriverDownloader::class.java)

    fun downloadIfNeeded(): File {
        val executable = executable()
        if (!executable.exists()) {
            log.info("Chrome Web Driver executable '${executable.absolutePath}' not found.")
            downloadAndUnzip()
            executable.setExecutable(true)
        }
        return executable
    }

    private fun downloadAndUnzip() {
        val effectiveUrl = "$url$packageName"
        log.info("Downloading Chrome Web Driver $version ($effectiveUrl) ...")
        val stream = ZipInputStream(URL(effectiveUrl).openStream())
        var entry: ZipEntry? = null
        while ({ entry = stream.nextEntry; entry }() != null) {
            File(entry!!.name).outputStream().let {
                stream.copyTo(it, 2048)
            }
        }
    }


    private fun executable(): File {
        return File(executableName)
    }

    companion object {
        fun create(version: String): WebDriverDownloader {
            val osName = System.getProperty("os.name")
            if (osName.contains("Linux", true)) {
                return LinuxDownloader(version)
            } else if (osName.contains("Windows", true)) {
                return WindowsDownloader(version)
            }
            throw UnsupportedSystem("Unsupported OS: '$osName'")
        }
    }
}

private class WindowsDownloader(version: String) : WebDriverDownloader(version, "chromedriver.exe", "chromedriver_win32.zip")
private class LinuxDownloader(version: String) : WebDriverDownloader(version, "chromedriver", "chromedriver_linux64.zip")