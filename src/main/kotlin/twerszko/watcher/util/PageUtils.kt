package twerszko.watcher.util

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory


internal object PageUtils {
    private val log = LoggerFactory.getLogger(PageUtils::class.java)

    fun makeVisible(webDriver: WebDriver, elementId: String) {
        jsExecutor(webDriver).executeScript("document.getElementById('$elementId').style.display='block';")
        jsExecutor(webDriver).executeScript("document.getElementById('$elementId').className = '';")
    }

    fun setValue(webDriver: WebDriver, elementId: String, value: String) {
        jsExecutor(webDriver).executeScript("document.getElementById('$elementId').value='$value';")
    }

    fun clickIfPossible(searchButton: WebElement): Boolean {
        return try {
            searchButton.click()
            true
        } catch (e: WebDriverException) {
            log.warn("Failed to click '$searchButton'", e.message)
            false
        } catch (e: Exception) {
            log.warn("Failed to click '$searchButton'", e)
            false
        }
    }

    private fun jsExecutor(webDriver: WebDriver) = (webDriver as JavascriptExecutor)
}