package twerszko.watcher.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import twerszko.watcher.util.PageUtils.makeVisible


class LoginPage(private val webDriver: WebDriver, private val baseUrl: String) {
    fun login(login: String, password: String): Search {
        webDriver.get("$baseUrl$URL")

        loginForm().findElement(By.id("Login")).sendKeys(login)
        makeVisible(webDriver, "Password")
        loginForm().findElement(By.id("Password")).sendKeys(password)
        loseFocus()
        loginButton().click()

        return Search(webDriver, baseUrl)
    }

    private fun loginForm(): WebElement {
        return webDriver.findElement(By.name("loginForm"))
    }

    private fun loseFocus() {
        webDriver.findElement(By.id("PageContainerWrapper")).click()
    }

    private fun loginButton() = loginForm().findElement(By.xpath(".//input[@type='submit']"))

    companion object {
        const val URL = "/PatientPortal/Account/LogOn"
    }
}