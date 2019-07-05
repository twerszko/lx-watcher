package twerszko.watcher.util

import org.openqa.selenium.*
import org.slf4j.LoggerFactory


internal object PageUtils {
    private val log = LoggerFactory.getLogger(PageUtils::class.java)

    fun runJs(webDriver: WebDriver, script: String): Any? {
        return jsExecutor(webDriver).executeScript(script)
    }

    fun makeVisible(webDriver: WebDriver, elementId: String) {
        jsExecutor(webDriver).executeScript("document.getElementById('$elementId').style.display='block';")
        jsExecutor(webDriver).executeScript("document.getElementById('$elementId').className = '';")
    }

    fun setValue(webDriver: WebDriver, elementId: String, value: String) {
        jsExecutor(webDriver).executeScript("document.getElementById('$elementId').value='$value';")
    }

    fun clickIfPossible(webDriver: WebDriver, element: WebElement): Boolean {
        return try {
            jsExecutor(webDriver).executeScript("arguments[0].click();", element)
            true
        } catch (e: WebDriverException) {
            log.warn("Failed to click '$element'", e.message)
            false
        } catch (e: Exception) {
            log.warn("Failed to click '$element'", e)
            false
        }
    }

    fun click(what: () -> WebElement) {
        return try {
            what().click()
        } catch (e: StaleElementReferenceException) {
            return what().click()
        }
    }

    private fun jsExecutor(webDriver: WebDriver) = (webDriver as JavascriptExecutor)
}