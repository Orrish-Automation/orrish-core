package com.orrish.automation.appiumselenium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
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
        if (reportEnabled) {
            String testName = getCurrentTestName();
            desiredCapabilities.setCapability("name", testName);
            //This check is for Selenoid grid execution
            if (executionCapabilities.containsKey("enableVideo") && executionCapabilities.get("enableVideo").toLowerCase().contains("true")) {
                String browserVersion = (SetUp.browserVersion != null && SetUp.browserVersion.trim().length() > 0) ? "_" + SetUp.browserVersion : "";
                String videoName = testName + "_" + SetUp.browser + browserVersion;
                desiredCapabilities.setCapability("videoName", videoName + ".mp4");
            }
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

    protected boolean navigateTo(String url) {
        webDriver.navigate().to(url);
        webDriver.manage().window().maximize();
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

    public ArrayNode checkAccessibilityForPage() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URL url = new URL("https://cdnjs.cloudflare.com/ajax/libs/axe-core/3.5.5/axe.min.js");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = in.readLine()) != null)
                stringBuilder.append(line);
        }
        executeJavascript(stringBuilder.toString());
        String results = String.valueOf(webDriver.executeAsyncScript("var callback = arguments[arguments.length - 1]; axe.run().then(results => callback(JSON.stringify(results.violations)));"));
        ArrayNode arrayNode = new ObjectMapper().readValue(results, ArrayNode.class);
        return arrayNode;
    }

    public Object getPageTitle() {
        return webDriver.getTitle();
    }

    public void scrollToElement(WebElement element) {
        webDriver.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public boolean executeJavascriptOnElement(String scriptToExecute, String locator) {
        webDriver.executeScript(scriptToExecute, webDriver.findElement(getElementBy(locator)));
        return true;
    }

    public boolean waitUntilIsDisplayed(String locator) {
        return CommonPageMethod.waitForElementSync(webDriver, webDriverWait, locator, true);
    }

    public boolean waitUntilIsGone(String locator) {
        return CommonPageMethod.waitForElementSync(webDriver, webDriverWait, locator, false);
    }

    public WebElement waitUntilOneOfTheElementsIsDisplayed(String locator) {
        return CommonPageMethod.waitUntilOneOfTheElementsIs(webDriver, locator, false);
    }

    public WebElement waitUntilOneOfTheElementsIsEnabled(String locator) {
        return CommonPageMethod.waitUntilOneOfTheElementsIs(webDriver, locator, true);
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
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            if (row.getText().contains(value)) {
                try {
                    row.click();
                } catch (Exception ex) {
                    //Sometimes row click does not do anything. Click the first cell on that row instead.
                    row.findElement(By.tagName("td")).click();
                }
                return true;
            }
        }
        return false;
    }

    public boolean clickRowWithHeaderContainingText(String headerText, String value) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement table = webDriver.findElement(By.tagName("table"));
        //Click row corresponding to cell with text for header
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
        try {
            waitForAndGetElement(getElementBy(locator)).click();
            return true;
        } catch (Exception ex) {
            //If there are multiple elements with this locator, click the first possible element.
            List<WebElement> elementList = webDriver.findElements(getElementBy(locator));
            if (elementList.size() > 1) {
                for (WebElement webElement : elementList) {
                    try {
                        webElement.click();
                        return true;
                    } catch (Exception ex1) {
                    }
                }
            }
        }
        return false;
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

    public String getTextFromElement(String locator) {
        return waitForAndGetElement(getElementBy(locator)).getText();
    }

    //method to verify string is present in the webpage or not
    public boolean isTextPresentInWebpage(String value) {
        waitForAndGetElement(By.xpath("(//*[contains(text(),'" + value + "')])")).isDisplayed();
        return true;
    }

    public boolean selectFromDropdown(String inputString, String locator) {
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
        textBoxesToClick.forEach(e -> selectCheckboxForText(e, shouldBeSelected));
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

    public boolean scrollToElement(String locator) {
        webDriver.executeScript("arguments[0].scrollIntoView(true);", webDriver.findElement(getElementBy(locator)));
        return true;
    }

    public void reportExecutionStatusWithScreenshotAndException(boolean isStepPassed, Object[] args, Throwable ex) {
        CommonPageMethod.reportExecutionStatusWithScreenshotAndException(isStepPassed, args, webDriver, ex);
    }

}
