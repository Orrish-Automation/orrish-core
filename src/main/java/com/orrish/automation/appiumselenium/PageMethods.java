package com.orrish.automation.appiumselenium;

import com.google.common.collect.ImmutableMap;
import com.orrish.automation.entrypoint.GeneralSteps;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
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
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.textMatches;

public class PageMethods {

    protected WebDriverWait webDriverWait;
    protected WebDriverWait appiumDriverWait;

    private PageMethods() {
    }

    private static PageMethods pageMethods;

    public static synchronized PageMethods getInstance() {
        if (pageMethods == null)
            pageMethods = new PageMethods();
        return pageMethods;
    }


    public void initializeDriverWait() {
        if (webDriver != null)
            webDriverWait = new WebDriverWait(webDriver, defaultWaitTime);
        if (appiumDriver != null)
            appiumDriverWait = new WebDriverWait(appiumDriver, defaultWaitTime);
    }

    public boolean navigateBack(WebDriver driver) {
        driver.navigate().back();
        return true;
    }

    public boolean refreshWebPage() {
        webDriver.navigate().refresh();
        return true;
    }

    public boolean closeAppOnDevice() {
        if (appiumDriver != null)
            appiumDriver.quit();
        return true;
    }

    public boolean pressBackKey() {
        if (appiumDriver.getPlatformName().toLowerCase().contains("android"))
            ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.BACK));
        return true;
    }

    public boolean pressHomeKey() {
        if (appiumDriver.getPlatformName().toLowerCase().contains("android"))
            ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.HOME));
        else if (appiumDriver.getPlatformName().toLowerCase().contains("ios"))
            appiumDriver.executeScript("mobile: pressButton", ImmutableMap.of("name", "home"));
        return true;
    }

    public boolean swipeOnceVertically() {
        Dimension dimension = appiumDriver.manage().window().getSize();
        int x = dimension.getWidth() / 2;
        int startY = (int) (dimension.getHeight() * 0.9);
        int endY = (int) (dimension.getHeight() * 0.1);
        TouchAction touchAction = new TouchAction(appiumDriver);
        touchAction.press(PointOption.point(x, startY))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
                .moveTo(PointOption.point(x, endY))
                .release()
                .perform();
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
                else if (GeneralSteps.isOnlyDigits(value))
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
        initializeDriverWait();
        return true;
    }

    public boolean launchAppOnDevice() throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("autoGrantPermissions", true);
        capabilities.setCapability("allowTestPackages", true);
        capabilities.setCapability("fullReset", true);
        if (platformName != null) {
            capabilities.setCapability("platformName", platformName);
        }
        if (deviceName != null) {
            capabilities.setCapability("deviceName", deviceName);
        }
        if (platformVersion != null) {
            capabilities.setCapability("platformVersion", platformVersion);
        }
        if (app != null) {
            capabilities.setCapability("app", app);
        }
        if (automationName != null) {
            capabilities.setCapability("automationName", automationName);
        }
        if (APP_ACTIVITY != null) {
            capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, APP_PACKAGE);
            capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, APP_ACTIVITY);
        }
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300);
        if (xcodeOrgId != null) {
            capabilities.setCapability("xcodeOrgId", xcodeOrgId);
        }
        if (xcodeSigningId != null) {
            capabilities.setCapability("xcodeSigningId", xcodeSigningId);
        }
        if (udid != null) {
            capabilities.setCapability("udid", udid);
        }
        if (executionCapabilities.size() > 0) {
            executionCapabilities.forEach((key, value) -> capabilities.setCapability(key, value));
        }
        if (platformName.toLowerCase().contains("android")) {
            appiumDriver = new AndroidDriver(new URL(appiumServerURL), capabilities);
        } else {
            appiumDriver = new IOSDriver(new URL(appiumServerURL), capabilities);
        }
        initializeDriverWait();
        return true;
    }

    protected boolean takeMobileScreenshotWithText(String text) {
        ReportUtility.reportWithScreenshot(appiumDriver, text, ReportUtility.REPORT_STATUS.INFO, text);
        return true;
    }

    protected boolean inBrowserNavigateTo(String url) {
        webDriver.navigate().to(url);
        webDriver.manage().window().maximize();
        return true;
    }

    protected boolean closeBrowser() {
        webDriver.close();
        return true;
    }

    protected boolean quitBrowser() {
        webDriver.quit();
        return true;
    }

    protected boolean takeWebScreenshotWithText(String text) {
        if (isScreenshotAtEachStepEnabled) {
            if (screenshotDelayInSeconds > 0) {
                waitSeconds(screenshotDelayInSeconds);
            }
            String testName = getCurrentTestName().replace(" ", "");
            String screenshotName = testName + "_Step" + ++stepCounter;
            ReportUtility.reportWithScreenshot(webDriver, screenshotName, ReportUtility.REPORT_STATUS.INFO, text);
        }
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

    public boolean waitUntilIsDisplayedFor(WebDriver driver, String locator) {
        return waitForElementSync(driver, locator, true);
    }

    public boolean waitUntilIsGoneFor(WebDriver driver, String locator) {
        return waitForElementSync(driver, locator, false);
    }

    protected boolean waitForElementSync(WebDriver driver, String locator, boolean shouldBeDisplayed) {
        List<String> locators = Arrays.asList(locator.split(",,"));
        locators.removeIf(e -> e.trim().length() == 0);
        for (String eachLocator : locators) {
            if (shouldBeDisplayed) {
                if (driver instanceof AppiumDriver)
                    appiumDriverWait.until(ExpectedConditions.visibilityOfElementLocated(getElementBy(eachLocator.trim())));
                else
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(getElementBy(eachLocator.trim())));
            } else {
                if (driver instanceof AppiumDriver) {
                    List<WebElement> elementList = appiumDriver.findElements(getElementBy(eachLocator.trim()));
                    appiumDriverWait.until(ExpectedConditions.invisibilityOfAllElements(elementList));
                } else {
                    List<WebElement> elementList = webDriver.findElements(getElementBy(eachLocator.trim()));
                    webDriverWait.until(ExpectedConditions.invisibilityOfAllElements(elementList));
                }
            }
        }
        return true;
    }

    public WebElement waitUntilOneOfTheLocatorsIsDisplayed(WebDriver driver, String locator) {
        return waitUntilOneOfTheLocatorsIs(driver, locator, false);
    }

    public WebElement waitUntilOneOfTheLocatorsIsEnabled(WebDriver driver, String locator) {
        return waitUntilOneOfTheLocatorsIs(driver, locator, true);
    }

    public boolean waitUntilElementTextContains(WebDriver driver, String locator, String text) {
        if (driver instanceof AppiumDriver)
            appiumDriverWait.until(textMatches(getElementBy(locator), Pattern.compile(".*" + text + ".*")));
        else
            webDriverWait.until(textMatches(getElementBy(locator), Pattern.compile(".*" + text + ".*")));
        return true;
    }

    public boolean waitUntilElementTextDoesNotContain(WebDriver driver, String locator, String text) {
        if (driver instanceof AppiumDriver)
            appiumDriverWait.until(ExpectedConditions.not(textMatches(getElementBy(locator), Pattern.compile(".*" + text + ".*"))));
        else
            webDriverWait.until(ExpectedConditions.not(textMatches(getElementBy(locator), Pattern.compile(".*" + text + ".*"))));
        return true;
    }

    public WebElement waitUntilOneOfTheLocatorsIs(WebDriver driver, String locator, boolean enabled) {
        String[] locatorList = locator.split(",,");
        for (int i = 0; i < 10; ++i) {
            waitSeconds(1);
            for (String eachLocator : locatorList) {
                try {
                    WebElement element = (driver instanceof AppiumDriver)
                            ? appiumDriver.findElement(getElementBy(eachLocator))
                            : webDriver.findElement(getElementBy(eachLocator));
                    if (enabled && element.isEnabled()) {
                        return element;
                    } else if (element.isDisplayed()) {
                        return element;
                    }
                } catch (Exception ex) {
                }
            }
        }
        return null;
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

    protected WebElement waitForAndGetElement(WebDriver remoteWebDriver, By byElement) {
        if (remoteWebDriver instanceof AppiumDriver) {
            appiumDriverWait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
            return appiumDriver.findElement(byElement);
        }
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
        return webDriver.findElement(byElement);
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

    public boolean clickWithText(WebDriver driver, String locator, String text) {
        //If locator has comma separated multiple values
        if (locator.contains(",")) {
            String[] locators = locator.split(",,");
            for (String eachLocator : locators) {
                WebElement targetElement = getElement(driver, eachLocator, text);
                if (targetElement != null) {
                    targetElement.click();
                    return true;
                }
            }
        } else {
            if (driver instanceof AppiumDriver)
                appiumDriverWait.until(ExpectedConditions.visibilityOfElementLocated(getElementBy(locator)));
            else
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(getElementBy(locator)));
            getElement(driver, locator, text).click();
            return true;
        }
        return false;
    }

    protected WebElement getElement(WebDriver driver, String locator, String text) {
        List<WebElement> webElementList = (driver instanceof AppiumDriver)
                ? appiumDriver.findElements(getElementBy(locator))
                : webDriver.findElements(getElementBy(locator));
        return getTargetElementWithText(webElementList, text, locator);
    }

    public boolean clickFor(WebDriver webDriver, String locator) {
        waitForAndGetElement(webDriver, getElementBy(locator)).click();
        return true;
    }

    public boolean clickWhicheverIsDisplayedIn(WebDriver driver, String locator) throws Exception {
        WebElement webElement = findFirstElementDisplayed(driver, locator);
        webElement.click();
        return true;
    }

    protected WebElement findFirstElementDisplayed(WebDriver driver, String locator) throws Exception {
        String[] elementLocators = locator.split(",,");
        //Trying 10 times before giving up.
        for (int i = 0; i < 10; i++) {
            for (String eachLocator : elementLocators) {
                try {
                    if (driver instanceof AppiumDriver)
                        return appiumDriver.findElement(getElementBy(eachLocator.trim()));
                    else
                        return webDriver.findElement(getElementBy(eachLocator.trim()));
                } catch (Exception ex) {
                }
            }
        }
        throw new Exception("Could not find any of the elements from " + locator);
    }

    public boolean enterInTextFieldNumber(String textToEnter, int index) {
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

    public boolean enterInTextFieldFor(WebDriver driver, String input, String locator) {
        try {
            //Sometimes, there may be more than one element with similar locator and first one may be there but not actionable
            WebElement webElement = waitForAndGetElement(driver, getElementBy(locator));
            webElement.clear();
            webElement.sendKeys(input);
            return true;
        } catch (Exception ex) {
            List<WebElement> elements = (driver instanceof AppiumDriver)
                    ? appiumDriver.findElements(getElementBy(locator))
                    : webDriver.findElements(getElementBy(locator));
            for (WebElement element : elements) {
                try {
                    element.sendKeys(input);
                    return true;
                } catch (Exception ex1) {
                }
            }
        }
        return false;
    }

    public String getTextFromLocator(WebDriver driver, String locator) {
        return waitForAndGetElement(driver, getElementBy(locator)).getText();
    }

    //method to verify string is present in the webpage or not
    public boolean isTextPresentInWebpage(String value) {
        waitForAndGetElement(webDriver, By.xpath("(//*[contains(text(),'" + value + "')])")).isDisplayed();
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
        Select select = (Select) waitForAndGetElement(webDriver, getElementBy(locator));
        select.selectByValue(inputString);
        return true;
    }

    protected By getElementBy(String locator) {
        locator = deduceDescriptiveElementLocatorValue(locator);
        if (locator.startsWith("/"))
            return By.xpath(locator);
        if (locator.startsWith("#"))
            return By.id(locator.replaceFirst("#", ""));
        if (locator.startsWith("."))
            return By.className(locator.replaceFirst(".", ""));
        if (locator.startsWith("name="))
            return By.name(locator.replace("name=", ""));
        if (locator.startsWith("text=")) {
            String valueToFind = locator.replace("text=", "");
            return By.xpath("//*[text() = '" + valueToFind + "' ]");
        }
        return By.cssSelector(locator);
    }

    protected String deduceDescriptiveElementLocatorValue(String locator) {
        if (locator.startsWith("id="))
            return locator.replaceFirst("id=", "#");
        if (locator.startsWith("xpath="))
            return locator.replaceFirst("xpath=", "");
        if (locator.startsWith("className="))
            return locator.replaceFirst("className=", ".");
        if (locator.startsWith("class="))
            return locator.replaceFirst("class=", ".");
        return locator;
    }

    public boolean selectUnselectCheckboxesForText(String stringToFind, boolean shouldBeSelected) {
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
        WebElement elementToSelect = getTargetElementWithText(elements, text, "input[type='checkbox']");
        if (elementToSelect == null)
            return;
        if ((toSelect && !elementToSelect.isSelected()) || (!toSelect && elementToSelect.isSelected()))
            elementToSelect.click();
    }

    protected List<WebElement> getFilteredWebElements() {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));
        return webDriver.findElements(By.tagName("input"));
    }

    protected WebElement getTargetElementWithText(List<WebElement> elements, String text, String locator) {
        for (WebElement webElement : elements) {
            if (webElement.getText().trim().equals(text.trim())) {
                return webElement;
            }
            WebElement parentElement = webElement.findElement(By.xpath(".."));
            for (int i = 0; i < 10; i++) {
                try {
                    String[] textsToCompare = parentElement.getText().contains("\n") ? parentElement.getText().split("\n") : new String[]{parentElement.getText().trim()};
                    if (Arrays.asList(textsToCompare).contains(text.trim())) {
                        if (parentElement.findElements(getElementBy(locator)).size() > 1)
                            break;
                        else {
                            return webElement;
                        }
                    }
                    parentElement = parentElement.findElement(By.xpath(".."));
                } catch (Exception ex) {
                    break;
                }
            }
        }
        return null;
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

}
