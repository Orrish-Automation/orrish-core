package com.orrish.automation.appiumselenium;

import com.google.common.collect.ImmutableMap;
import com.orrish.automation.utility.report.ReportUtility;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static com.orrish.automation.appiumselenium.CommonPageMethod.getElementBy;
import static com.orrish.automation.entrypoint.SetUp.*;

public class AppiumPageMethods {

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

    public AppiumDriver getAppiumDriver() {
        return appiumDriver;
    }

    public WebDriverWait getWebDriverWait() {
        return appiumDriverWait;
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
        appiumDriverWait = new WebDriverWait(appiumDriver, Duration.ofSeconds(defaultWaitTime));
        return true;
    }

    protected boolean takeMobileScreenshotWithText(String text) {
        CommonPageMethod.takeScreenshotWithText(text, appiumDriver);
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
        if (appiumDriver.getCapabilities().getPlatformName().name().toLowerCase().contains("android"))
            ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.BACK));
        return true;
    }

    public boolean pressHomeKey() {
        if (appiumDriver.getCapabilities().getPlatformName().name().toLowerCase().contains("android"))
            ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.HOME));
        else if (appiumDriver.getCapabilities().getPlatformName().name().toLowerCase().contains("ios"))
            appiumDriver.executeScript("mobile: pressButton", ImmutableMap.of("name", "home"));
        return true;
    }

    public boolean swipeOnceVertically() {
        Dimension dimension = appiumDriver.manage().window().getSize();
        int startY = (int) (dimension.getHeight() * 0.9);
        int endY = (int) (dimension.getHeight() * 0.1);
        TouchActions touchActions = new TouchActions(appiumDriver);
        touchActions.scrollByAmount(0, endY - startY)
                .release()
                .perform();
        return true;
    }


    public boolean inMobileWaitUntilTextIsDisplayed(String locator) {
        return CommonPageMethod.waitForElementSync(appiumDriver, appiumDriverWait, locator, true);
    }

    public boolean inMobileWaitUntilTextIsGone(String locator) {
        return CommonPageMethod.waitForElementSync(appiumDriver, appiumDriverWait, locator, false);
    }

    public boolean waitUntilOneOfTheElementsIsDisplayed(String locator) {
        WebElement element = CommonPageMethod.waitUntilOneOfTheElementsIs(appiumDriver, locator, false);
        return element != null;
    }

    public boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        WebElement webElement = CommonPageMethod.waitUntilOneOfTheElementsIs(appiumDriver, locator, true);
        return webElement != null;
    }

    public boolean waitUntilElementContains(String locator, String text) {
        CommonPageMethod.waitUntilElementTextContains(appiumDriverWait, locator, text);
        return true;
    }

    public boolean waitUntilElementDoesNotContain(String locator, String text) {
        CommonPageMethod.waitUntilElementTextDoesNotContain(appiumDriverWait, locator, text);
        return true;
    }

    public boolean tap(String locator) {
        CommonPageMethod.waitForAndGetElement(appiumDriver, appiumDriverWait, CommonPageMethod.getElementBy(locator)).click();
        return true;
    }

    public boolean tapWithText(String locator, String text) {
        return CommonPageMethod.clickWithText(appiumDriver, appiumDriverWait, locator, text);
    }

    public boolean tapWhicheverIsDisplayedIn(String locator) throws Exception {
        WebElement webElement = CommonPageMethod.findFirstElementDisplayed(appiumDriver, locator);
        webElement.click();
        return true;
    }

    public boolean enterInTextFieldIn(String input, String locator) {
        return CommonPageMethod.enterInTextField(appiumDriver, appiumDriverWait, input, locator);
    }

    public String getTextFromElement(String locator) {
        return CommonPageMethod.waitForAndGetElement(appiumDriver, appiumDriverWait, getElementBy(locator)).getText();
    }

    public void reportExecutionStatusWithScreenshotAndException(boolean isPassed, Object[] args, Exception ex) {
        CommonPageMethod.reportExecutionStatusWithScreenshotAndException(isPassed, args, appiumDriver, ex);
    }

}
