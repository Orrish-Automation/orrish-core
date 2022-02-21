package com.orrish.automation.appiumselenium;

import com.orrish.automation.entrypoint.GeneralSteps;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.orrish.automation.appiumselenium.CommonPageMethod.getElementBy;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;

public class SeleniumPageMethods {

    protected RemoteWebDriver webDriver;
    protected WebDriverWait webDriverWait;

    protected SeleniumPageMethods() {
    }

    private static SeleniumPageMethods seleniumPageMethods;

    public static SeleniumPageMethods getInstance() {
        if (seleniumPageMethods == null) {
            synchronized (SeleniumPageMethods.class) {
                if (seleniumPageMethods == null) {
                    seleniumPageMethods = new SeleniumPageMethods();
                }
            }
        }
        return seleniumPageMethods;
    }

    public RemoteWebDriver getWebDriver() {
        return webDriver;
    }

    public WebDriverWait getWebDriverWait() {
        return webDriverWait;
    }

    public boolean quitBrowser() {

        if (webDriver != null && webDriver.getSessionId() != null) {
            try {
                webDriver.quit();
                webDriver = null;
            } catch (Exception ex) {
                ReportUtility.reportExceptionDebug(ex);
            }
        }
        return true;
    }

    public boolean launchBrowserAndNavigateTo(String url) throws MalformedURLException {

        String testName = getCurrentTestName();
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("name", testName);
        desiredCapabilities.setCapability("acceptInsecureCerts", true);

        switch (browser.trim().toUpperCase()) {
            case "CHROME":
                desiredCapabilities.setBrowserName(BrowserType.CHROME);
                break;
            case "FIREFOX":
                desiredCapabilities.setBrowserName(BrowserType.FIREFOX);
                break;
            case "SAFARI":
                desiredCapabilities.setBrowserName(BrowserType.SAFARI);
                break;
        }
        //This check is for Selenoid grid execution
        if (executionCapabilities.containsKey("enableVideo") && executionCapabilities.get("enableVideo").toLowerCase().contains("true")) {
            String browserVersion = (SetUp.browserVersion != null && SetUp.browserVersion.trim().length() > 0) ? "_" + SetUp.browserVersion : "";
            String videoName = testName + "_" + SetUp.browser + browserVersion;
            desiredCapabilities.setCapability("videoName", videoName + ".mp4");
        }
        if (executionCapabilities.size() > 0) {
            executionCapabilities.entrySet().forEach(e -> {
                String key = e.getKey();
                String value = e.getValue().trim().toLowerCase();
                if (value.contentEquals("true") || value.contentEquals("false"))
                    desiredCapabilities.setCapability(key, Boolean.parseBoolean(value));
                else if (new GeneralSteps().isOnlyDigits(value))
                    desiredCapabilities.setCapability(key, Integer.parseInt(value));
                else
                    desiredCapabilities.setCapability(key, value);
            });
        }

        if (browserVersion != null && browserVersion.trim().length() > 1)
            desiredCapabilities.setVersion(browserVersion);
        webDriver = new RemoteWebDriver(new URL(seleniumGridURL), desiredCapabilities);
        webDriver.setFileDetector(new LocalFileDetector());
        webDriver.navigate().to(url);
        if (browserWidth > 0 && browserHeight > 0) {
            webDriver.manage().window().setSize(new Dimension(browserWidth, browserHeight));
        } else {
            webDriver.manage().window().maximize();
        }
        webDriverWait = new WebDriverWait(webDriver, defaultWaitTime);
        return true;
    }

    public boolean navigateBack() {
        webDriver.navigate().back();
        return true;
    }

    public boolean refreshWebPage() {
        webDriver.navigate().refresh();
        return true;
    }

    protected boolean navigateTo(String url) {
        webDriver.navigate().to(url);
        webDriver.manage().window().maximize();
        return true;
    }

    protected boolean closeBrowser() {
        webDriver.close();
        return true;
    }

    protected boolean takeWebScreenshotWithText(String text) {
        CommonPageMethod.takeScreenshotWithText(text, webDriver);
        return true;
    }

    public boolean executeJavascript(String scriptToExecute) {
        webDriver.executeScript(scriptToExecute);
        return true;
    }

    public void scrollToElement(WebElement element) {
        webDriver.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public boolean scrollTo(String locator) {
        webDriver.executeScript("arguments[0].scrollIntoView(true);", webDriver.findElement(getElementBy(locator)));
        return true;
    }

    public boolean scrollToBottom() {
        return executeJavascript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public boolean waitUntilIsDisplayed(String locator) {
        return CommonPageMethod.waitForElementSync(webDriver, webDriverWait, locator, true);
    }

    public boolean waitUntilIsGone(String locator) {
        return CommonPageMethod.waitForElementSync(webDriver, webDriverWait, locator, false);
    }

    public WebElement waitUntilOneOfTheLocatorsIsDisplayed(String locator) {
        return CommonPageMethod.waitUntilOneOfTheLocatorsIs(webDriver, locator, false);
    }

    public WebElement waitUntilOneOfTheLocatorsIsEnabled(String locator) {
        return CommonPageMethod.waitUntilOneOfTheLocatorsIs(webDriver, locator, true);
    }

    public boolean waitUntilElementTextContains(String locator, String text) {
        CommonPageMethod.waitUntilElementTextContains(webDriverWait, locator, text);
        return true;
    }

    public boolean waitUntilElementTextDoesNotContain(String locator, String text) {
        CommonPageMethod.waitUntilElementTextDoesNotContain(webDriverWait, locator, text);
        return true;
    }

    public boolean isElementDisplayedFor(String locator) {
        return CommonPageMethod.isElementDisplayedFor(webDriver, locator);
    }

    public boolean isElementEnabledFor(String locator) {
        return CommonPageMethod.isElementEnabledFor(webDriver, webDriverWait, locator);
    }

    public boolean isElementSelectedFor(String locator) {
        return CommonPageMethod.isElementSelectedFor(webDriver, webDriverWait, locator);
    }

    public boolean isAlertPresent() {
        try {
            webDriver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException ex) {
            return false;
        }
    }

    public String getAlertText() {
        Alert alert = webDriver.switchTo().alert();
        return alert.getText();
    }

    public boolean dismissAlertIfPresent() {
        //Do not put error check here as it blocks screenshot step.
        if (isAlertPresent()) {
            Alert alert = webDriver.switchTo().alert();
            alert.dismiss();
        }
        return true;
    }

    public boolean acceptAlertIfPresent() {
        //Do not put error check here as it blocks screenshot step.
        if (isAlertPresent()) {
            Alert alert = webDriver.switchTo().alert();
            alert.accept();
        }
        return true;
    }

    public WebElement waitForAndGetElement(By byElement) {
        return CommonPageMethod.waitForAndGetElement(webDriver, webDriverWait, byElement);
    }

    public boolean clickRowContainingText(String value) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = webDriver.findElement(By.tagName("table"));
        if (table.findElement(By.tagName("th")).isDisplayed()) {
            List<WebElement> headers = table.findElements(By.tagName("th"));
            int index = 0;
            for (; index < headers.size(); index++) {
                if (headers.get(index).getText().contains(value))
                    break;
            }
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            for (WebElement row : rows) {
                if (rows.get(index).getText().contains(value)) {
                    row.click();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean clickWithText(String locator, String text) {
        return CommonPageMethod.clickWithText(webDriver, webDriverWait, locator, text);
    }

    public boolean click(String locator) {
        waitForAndGetElement(getElementBy(locator)).click();
        return true;
    }

    public boolean clickWhicheverIsDisplayedIn(String locator) throws Exception {
        WebElement webElement = CommonPageMethod.findFirstElementDisplayed(webDriver, locator);
        webElement.click();
        return true;
    }

    public boolean enterInTextFieldNumber(String textToEnter, String indexNumber) {
        int index = Integer.parseInt(indexNumber);
        List<WebElement> inputElements = getFilteredWebElements();
        inputElements.removeIf(e -> !"text or password".contains(e.getAttribute("type")));
        //Decrement to make zero index based.
        --index;
        int counter = 0;
        for (WebElement webElement : inputElements) {
            try {
                //Ensure webElement is possible to interact
                webElement.isEnabled();
                if (index == counter++) {
                    webElement.clear();
                    webElement.sendKeys(textToEnter);
                    return true;
                }
            } catch (Exception ex) {
            }
        }
        return false;
    }

    public boolean enterInTextField(String input, String locator) {
        return CommonPageMethod.enterInTextField(webDriver, webDriverWait, input, locator);
    }

    public String getTextFromLocator(String locator) {
        return waitForAndGetElement(getElementBy(locator)).getText();
    }

    //method to verify string is present in the webpage or not
    public boolean isTextPresentInWebpage(String value) {
        waitForAndGetElement(By.xpath("(//*[contains(text(),'" + value + "')])")).isDisplayed();
        return true;
    }

    public boolean selectFromDropdown(String inputString, String locator) {
        //mat elements
        if (locator.contains("mat-select")) {
            webDriver.findElement(getElementBy(locator)).click();
            List<WebElement> options = webDriver.findElements(By.tagName("mat-option"));
            for (WebElement option : options) {
                if (option.getText().contains(inputString)) {
                    option.click();
                    return true;
                }
            }
        } else {
            return false;
        }
        //Native select
        Select select = (Select) waitForAndGetElement(getElementBy(locator));
        select.selectByValue(inputString);
        return true;
    }

    public boolean selectCheckboxForText(String stringToFind) {
        return selectUnselectCheckboxesForText(stringToFind, true);
    }

    public boolean unselectCheckboxForText(String stringToFind) {
        return selectUnselectCheckboxesForText(stringToFind, false);
    }

    private boolean selectUnselectCheckboxesForText(String stringToFind, boolean shouldBeSelected) {
        List<String> textBoxesToClick = Arrays.asList(stringToFind.split(",,"));
        //mat checkboxes
        List<WebElement> checkboxes = webDriver.findElements(By.tagName("mat-checkbox"));
        if (checkboxes.size() > 0) {
            for (WebElement checkbox : checkboxes) {
                if (textBoxesToClick.contains(checkbox.getText())) {
                    if ((!checkbox.getAttribute("class").contains("mat-checkbox-checked") && shouldBeSelected)
                            || (checkbox.getAttribute("class").contains("mat-checkbox-checked") && !shouldBeSelected)) {
                        checkbox.findElement(By.className("mat-checkbox-inner-container")).click();
                    }
                }
            }

        } else {
            //Regular checkboxes
            textBoxesToClick.forEach(e -> selectCheckboxForText(e, shouldBeSelected));
        }
        return true;
    }

    protected void selectCheckboxForText(String text, boolean toSelect) {
        List<WebElement> elements = getFilteredWebElements();
        elements.removeIf(e -> !"checkbox".contains(e.getAttribute("type")));
        WebElement elementToSelect = CommonPageMethod.getTargetElementWithText(elements, text, "input[type='checkbox']");
        if (elementToSelect == null)
            return;
        if ((toSelect && !elementToSelect.isSelected()) || (!toSelect && elementToSelect.isSelected()))
            elementToSelect.click();
    }

    protected List<WebElement> getFilteredWebElements() {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));
        return webDriver.findElements(By.tagName("input"));
    }

    public String getCurrentWindowId() {
        return webDriver.getWindowHandle();
    }

    public boolean switchToWindowId(String windowId) {
        webDriver.switchTo().window(windowId);
        return true;
    }

    public boolean maximizeTheWindow() {
        webDriver.manage().window().maximize();
        return true;
    }

    public boolean selectDropdownByText(String option) {
        WebElement element = webDriver.findElement(By.xpath("//mat-option[@role='option']/span[contains(text(),'" + option + "')]"));
        scrollToElement(element);
        webDriver.findElement(By.xpath("//mat-option[@role='option']/span[contains(text(),'" + option + "')]")).click();
        return true;
    }

    public boolean scrollToElement(String locator) {
        webDriver.executeScript("arguments[0].scrollIntoView(true);", webDriver.findElement(getElementBy(locator)));
        return true;
    }

    public void reportExecutionStatus(boolean isStepPassed, Object[] args) {
        CommonPageMethod.reportExecutionStatus(isStepPassed, args, webDriver);
    }

    public void reportException(Object[] args, Exception ex) {
        CommonPageMethod.reportException(webDriver, args, ex);
    }

}
