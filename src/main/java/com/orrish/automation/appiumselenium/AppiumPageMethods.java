package com.orrish.automation.appiumselenium;

import com.google.common.collect.ImmutableMap;
import com.orrish.automation.utility.report.ReportUtility;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.orrish.automation.entrypoint.SetUp.*;
import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;

public class AppiumPageMethods extends CommonPageMethod {

    protected AppiumDriver appiumDriver;
    protected WebDriverWait appiumDriverWait;

    protected AppiumPageMethods() {
    }

    private static AppiumPageMethods appiumPageMethods;

    public static AppiumPageMethods getInstance() {
        if (appiumPageMethods == null)
            synchronized (AppiumPageMethods.class) {
                if (appiumPageMethods == null)
                    appiumPageMethods = new AppiumPageMethods();
            }
        return appiumPageMethods;
    }

    public boolean launchAppOnDevice() throws MalformedURLException {
        appiumDriver = platform.toLowerCase().contains("android") ? launchAppOnAndroidDevice() : launchAppOniOSDevice();
        appiumDriverWait = new WebDriverWait(appiumDriver, Duration.ofSeconds(defaultWaitTime));
        return true;
    }

    private AppiumDriver launchAppOnAndroidDevice() throws MalformedURLException {

        UiAutomator2Options options = new UiAutomator2Options();
        if (deviceName != null) options.setDeviceName(deviceName);
        if (platformVersion != null) options.setPlatformVersion(platformVersion);
        if (app != null) options.setApp(app);
        if (automationName != null) options.setAutomationName(automationName);
        if (APP_PACKAGE != null) options.setAppPackage(APP_PACKAGE);
        if (APP_ACTIVITY != null) options.setAppActivity(APP_ACTIVITY);
        executionCapabilities.forEach((key, value) -> options.amend(key, value));
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        return new AndroidDriver(new URL(appiumServerURL), options);
    }

    private AppiumDriver launchAppOniOSDevice() throws MalformedURLException {

        XCUITestOptions options = new XCUITestOptions();
        if (deviceName != null) options.setDeviceName(deviceName);
        if (platformVersion != null) options.setPlatformVersion(platformVersion);
        if (app != null) options.setApp(app);
        if (automationName != null) options.setAutomationName(automationName);
        if (udid != null) options.setUdid(udid);
        executionCapabilities.forEach((key, value) -> options.amend(key, value));
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        return new IOSDriver(new URL(appiumServerURL), options);
    }

    public boolean takeMobileScreenshotWithText(String text) {
        takeScreenshotWithText(text, appiumDriver);
        return true;
    }

    public boolean goBackToPreviousPage() {
        appiumDriver.navigate().back();
        return true;
    }

    public boolean quitAppOnDevice() {
        if (appiumDriver != null && appiumDriver.getSessionId() != null) {
            try {
                appiumDriver.quit();
                appiumDriver = null;
            } catch (Exception ex) {
                ReportUtility.reportExceptionDebug(ex);
            }
        }
        return true;
    }

    public boolean pressBackKey() {
        if (isPlatformAndroid())
            ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.BACK));
        return true;
    }

    public boolean pressHomeKey() {
        if (isPlatformAndroid())
            ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.HOME));
        else if (isPlatformiOS())
            appiumDriver.executeScript("mobile: pressButton", ImmutableMap.of("name", "home"));
        return true;
    }

    public boolean swipeOnceVertically() {
        Dimension dimension = appiumDriver.manage().window().getSize();
        int startY = (int) (dimension.getHeight() * 0.9);
        int endY = (int) (dimension.getHeight() * 0.1);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);
        sequence.addAction(finger.createPointerMove(ofMillis(0),
                PointerInput.Origin.viewport(), dimension.getWidth() / 2, startY));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.MIDDLE.asArg()));
        sequence.addAction(new Pause(finger, ofMillis(600)));
        sequence.addAction(finger.createPointerMove(ofMillis(600),
                PointerInput.Origin.viewport(), dimension.getWidth() / 2, endY));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.MIDDLE.asArg()));
        appiumDriver.perform(singletonList(sequence));
        return true;
    }

    public boolean tap(String locator) {
        getAppiumElement(locator).click();
        return true;
    }

    public boolean tapNumber(String locator, String number) {
        List<WebElement> elementList = appiumDriver.findElements(getAppiumElementBy(locator));
        elementList.get(Integer.parseInt(number) - 1).click();
        return true;
    }

    public boolean tapWithPartialText(String locator) {
        By by = By.xpath("//*[contains(@text,'" + locator + "')]");
        appiumDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        appiumDriver.findElement(by).click();
        return true;
    }

    public boolean tapWhicheverIsDisplayedIn(String locator) throws Exception {
        WebElement webElement = findFirstElementDisplayed(appiumDriver, getAllByFromLocator(locator));
        webElement.click();
        return true;
    }

    public boolean inMobileEnterInTextFieldNumber(String input, String number) {
        String locator = isPlatformAndroid() ? "//android.widget.EditText" : "//XCUIElementTypeTextField";
        List<WebElement> webElementList = appiumDriver.findElements(By.xpath(locator));
        webElementList.get(Integer.parseInt(number) - 1).sendKeys(input);
        return true;
    }

    public boolean inMobileEnterInTextField(String input, String locator) {
        WebElement webElement = getAppiumElement(locator);
        webElement.clear();
        webElement.sendKeys(input);
        return true;
    }

    public String inMobileGetFullTextFrom(String locator) {
        By by = isAppiumLocator(locator) ? getAppiumElementBy(locator) : By.xpath("//*[contains(@text,'" + locator + "')]");
        return appiumDriver.findElement(by).getText();
    }

    public boolean inMobileWaitUntilIsDisplayed(String locator) {
        return waitForElementDisplayedOrGone(appiumDriver, appiumDriverWait, getAppiumElementBy(locator), true);
    }

    public boolean inMobileWaitUntilIsGone(String locator) {
        return waitForElementDisplayedOrGone(appiumDriver, appiumDriverWait, getAppiumElementBy(locator), false);
    }

    public boolean inMobileWaitUntilOneOfIsDisplayed(String locator) {
        WebElement element = waitUntilOneOfTheElementsIs(appiumDriver, getAllByFromLocator(locator), false);
        return element != null;
    }

    public boolean inMobileWaitUntilOneOfIsEnabled(String locator) {
        WebElement webElement = waitUntilOneOfTheElementsIs(appiumDriver, getAllByFromLocator(locator), true);
        return webElement != null;
    }

    public boolean inMobileWaitUntilContains(String locator, String text) {
        By by = isAppiumLocator(locator) ? getAppiumElementBy(locator) : By.xpath("//*[contains(@text,'" + locator + "')]");
        waitUntilElementTextContains(appiumDriverWait, by, text);
        return true;
    }

    public boolean inMobileWaitUntilDoesNotContain(String locator, String text) {
        By by = isAppiumLocator(locator) ? getAppiumElementBy(locator) : By.xpath("//*[contains(@text,'" + locator + "')]");
        waitUntilElementTextDoesNotContain(appiumDriverWait, by, text);
        return true;
    }

    private boolean isPlatformAndroid() {
        return appiumDriver.getCapabilities().getPlatformName().name().equalsIgnoreCase("android");
    }

    private boolean isPlatformiOS() {
        return appiumDriver.getCapabilities().getPlatformName().name().equalsIgnoreCase("ios");
    }

    private void reportExecutionStatusWithScreenshotAndException(boolean isPassed, Object[] args, Exception ex) {
        reportExecutionStatusWithScreenshotAndException(isPassed, args, appiumDriver, ex);
    }

    private boolean isAppiumLocator(String locatorString) {
        if (locatorString.startsWith("text="))
            return false;
        boolean isId = locatorString.startsWith("#") || locatorString.startsWith("id=");
        boolean isClass = locatorString.startsWith(".") || locatorString.startsWith("class=");
        boolean isName = locatorString.startsWith("name=");
        boolean isXpath = locatorString.trim().startsWith("//");
        return isId || isClass || isName || isXpath;
    }

    private By getAppiumElementBy(String locator) {
        return isAppiumLocator(locator) ? getElementBy(locator) : By.xpath("//*[@text='" + locator.replace("text=", "") + "']");
    }

    private List<By> getAllByFromLocator(String locator) {
        String[] elementLocators = locator.split(",,");
        List<By> by = new ArrayList<>();
        for (String elementLocator : elementLocators) {
            by.add(getAppiumElementBy(elementLocator));
        }
        return by;
    }

    private WebElement getAppiumElement(String locator) {
        By by = getAppiumElementBy(locator);
        appiumDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return appiumDriver.findElement(by);
    }

}
