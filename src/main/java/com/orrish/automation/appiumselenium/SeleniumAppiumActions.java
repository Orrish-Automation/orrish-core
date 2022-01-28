package com.orrish.automation.appiumselenium;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.model.TestStepReportModel;
import com.orrish.automation.utility.GeneralUtility;
import com.orrish.automation.utility.report.ReportUtility;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;

public class SeleniumAppiumActions {

    protected boolean isWebStepPassed = true;
    protected boolean isMobileStepPassed = true;
    protected PageMethods pageMethods;

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
        if (executionCapabilities.size() > 0) {
            executionCapabilities.forEach(desiredCapabilities::setCapability);
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
        if (pageMethods == null)
            pageMethods = new PageMethods();
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
        if (pageMethods == null)
            pageMethods = new PageMethods();
        return true;
    }

    protected boolean navigateTo(String url) {
        webDriver.navigate().to(url);
        webDriver.manage().window().maximize();
        return true;
    }

    protected boolean navigateBackInWeb() {
        return pageMethods.navigateBack(webDriver);
    }

    protected boolean refreshPage() {
        return pageMethods.refreshPage();
    }

    protected boolean takeWebScreenshotWithText(String text) {
        if (isScreenshotAtEachStepEnabled) {
            if (screenshotDelayInSeconds > 0) {
                GeneralUtility.waitSeconds(screenshotDelayInSeconds);
            }
            String testName = getCurrentTestName().replace(" ", "");
            String screenshotName = testName + "_Step" + ++stepCounter;
            ReportUtility.reportWithScreenshot(webDriver, screenshotName, ReportUtility.REPORT_STATUS.INFO, text);
        }
        return true;
    }

    public String executeOnWebAndReturnString(Object... args) {
        if (!isWebStepPassed) {
            ReportUtility.reportInfo(GeneralUtility.getMethodStyleStepName(args) + " ignored due to last failure.");
            return "";
        }
        String value = executeOnWebAndReturnObject(args).toString();
        if (isScreenshotAtEachStepEnabled) {
            TestStepReportModel testStepReportModel = new TestStepReportModel(++stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.INFO, webDriver);
        }
        ReportUtility.reportInfo(GeneralUtility.getMethodStyleStepName(args) + " returned " + value);
        return value;
    }

    public String executeOnMobileAndReturnString(Object... args) {
        if (!isMobileStepPassed) {
            ReportUtility.reportInfo(GeneralUtility.getMethodStyleStepName(args) + " ignored due to last failure.");
            return "";
        }
        String value = executeOnMobileAndReturnObject(args).toString();
        if (isScreenshotAtEachStepEnabled) {
            TestStepReportModel testStepReportModel = new TestStepReportModel(++stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.INFO, appiumDriver);
        }
        ReportUtility.reportInfo(GeneralUtility.getMethodStyleStepName(args) + " returned " + value);
        return value;
    }

    public boolean executeOnWebAndReturnBoolean(Object... args) {
        if (isWebStepPassed) {
            return Boolean.parseBoolean(executeOnWebAndReturnObject(args).toString());
        }
        ReportUtility.reportInfo(GeneralUtility.getMethodStyleStepName(args) + " ignored due to last failure.");
        return false;
    }

    public boolean executeOnMobileAndReturnBoolean(Object... args) {
        if (isMobileStepPassed) {
            return Boolean.parseBoolean(executeOnMobileAndReturnObject(args).toString());
        }
        ReportUtility.reportInfo(GeneralUtility.getMethodStyleStepName(args) + " ignored due to last failure.");
        return false;
    }

    protected Object executeOnMobileAndReturnObject(Object... args) {
        try {
            switch (args[0].toString()) {
                case "launchAppOnDevice":
                    isMobileStepPassed = launchAppOnDevice();
                    break;
                case "takeMobileScreenshotWithText":
                    isMobileStepPassed = takeMobileScreenshotWithText(args[1].toString());
                    break;
                case "closeAppOnDevice":
                    isMobileStepPassed = closeAppOnDevice();
                    break;
                case "goBackToPreviousPageInMobile":
                    isMobileStepPassed = goBackToPreviousPageInMobile();
                    break;
                case "pressHomeKey":
                    isMobileStepPassed = pressHomeKey();
                    break;
                case "pressBackKey":
                    isMobileStepPassed = pressBackKey();
                    break;
                case "swipeOnceVertically":
                    isMobileStepPassed = swipeOnceVertically();
                    break;
                case "tapFor":
                    isMobileStepPassed = pageMethods.clickFor(appiumDriver, args[1].toString());
                    break;
                case "tapWithText":
                    isMobileStepPassed = pageMethods.clickWithText(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "tapWhicheverIsDisplayedIn":
                    isMobileStepPassed = pageMethods.clickWhicheverIsDisplayedIn(appiumDriver, args[1].toString());
                    break;
                case "inMobileWaitUntilIsGoneFor":
                    isMobileStepPassed = pageMethods.waitUntilIsGoneFor(appiumDriver, args[1].toString());
                    break;
                case "inMobileWaitUntilIsDisplayedFor":
                    isMobileStepPassed = pageMethods.waitUntilIsDisplayedFor(appiumDriver, args[1].toString());
                    break;
                case "inMobileWaitUntilOneOfTheLocatorsIsEnabled":
                    isMobileStepPassed = (pageMethods.waitUntilOneOfTheLocatorsIsEnabled(appiumDriver, args[1].toString()) != null);
                    break;
                case "inMobileWaitUntilElementTextContains":
                    isMobileStepPassed = pageMethods.waitUntilElementTextContains(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "inMobileWaitUntilElementTextDoesNotContain":
                    isMobileStepPassed = pageMethods.waitUntilElementTextDoesNotContain(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "inMobileEnterInTextFieldFor":
                    isMobileStepPassed = pageMethods.enterInTextFieldFor(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "inMobileGetTextFromLocator":
                    return pageMethods.getTextFromLocator(appiumDriver, args[1].toString());
                default:
                    throw new Exception(args[0].toString() + " method not implemented yet.");
            }
        } catch (Exception ex) {
            isMobileStepPassed = false;
            return reportException(appiumDriver, args, ex);
        }
        reportExecutionStatus(isMobileStepPassed, args, appiumDriver);
        return isMobileStepPassed;
    }

    protected Object executeOnWebAndReturnObject(Object... args) {
        try {
            switch (args[0].toString()) {
                case "launchBrowserAndNavigateTo":
                    isWebStepPassed = launchBrowserAndNavigateTo(args[1].toString());
                    break;
                case "inBrowserNavigateTo":
                    isWebStepPassed = navigateTo(args[1].toString());
                    break;
                case "maximizeTheWindow":
                    isWebStepPassed = pageMethods.maximizeTheWindow();
                    break;
                case "takeWebScreenshotWithText":
                    return takeWebScreenshotWithText(args[1].toString());
                case "clickFor":
                    isWebStepPassed = pageMethods.clickFor(webDriver, args[1].toString());
                    break;
                case "clickWithText":
                    isWebStepPassed = pageMethods.clickWithText(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "clickWhicheverIsDisplayedIn":
                    isWebStepPassed = pageMethods.clickWhicheverIsDisplayedIn(webDriver, args[1].toString());
                    break;
                case "clickRowContainingText":
                    return pageMethods.clickRowContainingText(args[1].toString());
                case "selectCheckboxForText":
                    isWebStepPassed = pageMethods.selectUnselectCheckboxesForText(args[1].toString(), true);
                    break;
                case "unselectCheckboxForText":
                    isWebStepPassed = pageMethods.selectUnselectCheckboxesForText(args[1].toString(), false);
                    break;
                case "waitUntilIsGoneFor":
                    isWebStepPassed = pageMethods.waitUntilIsGoneFor(webDriver, args[1].toString());
                    break;
                case "waitUntilIsDisplayedFor":
                    isWebStepPassed = pageMethods.waitUntilIsDisplayedFor(webDriver, args[1].toString());
                    break;
                case "waitUntilOneOfTheLocatorsIsDisplayed":
                    isWebStepPassed = (pageMethods.waitUntilOneOfTheLocatorsIsDisplayed(webDriver, args[1].toString()) != null);
                    break;
                case "waitUntilOneOfTheLocatorsIsEnabled":
                    isWebStepPassed = (pageMethods.waitUntilOneOfTheLocatorsIsEnabled(webDriver, args[1].toString()) != null);
                    break;
                case "waitUntilElementTextContains":
                    isWebStepPassed = pageMethods.waitUntilElementTextContains(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "waitUntilElementTextDoesNotContain":
                    isWebStepPassed = pageMethods.waitUntilElementTextDoesNotContain(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "enterInTextFieldFor":
                    isWebStepPassed = pageMethods.enterInTextFieldFor(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "enterInTextFieldNumber":
                    isWebStepPassed = pageMethods.enterInTextFieldNumber(args[1].toString(), Integer.parseInt(args[2].toString()));
                    break;
                case "isTextPresentInWebpage":
                    isWebStepPassed = pageMethods.isTextPresentInWebpage(args[1].toString());
                    break;
                case "getAlertText":
                    return pageMethods.getAlertText();
                case "dismissAlertIfPresent":
                    isWebStepPassed = pageMethods.dismissAlertIfPresent();
                    break;
                case "acceptAlertIfPresent":
                    isWebStepPassed = pageMethods.acceptAlertIfPresent();
                    break;
                case "selectFromDropdown":
                    isWebStepPassed = pageMethods.selectFromDropdown(args[1].toString(), args[2].toString());
                    break;
                case "selectDropdownByText":
                    isWebStepPassed = pageMethods.selectDropdownByText(args[1].toString());
                    break;
                case "getTextFromLocator":
                    return pageMethods.getTextFromLocator(webDriver, args[1].toString());
                case "executeJavascript":
                    isWebStepPassed = pageMethods.executeJavascript(args[1].toString());
                    break;
                case "scrollTo":
                    isWebStepPassed = pageMethods.scrollTo(args[1].toString());
                    break;
                case "scrollToBottom":
                    isWebStepPassed = pageMethods.executeJavascript("window.scrollTo(0, document.body.scrollHeight)");
                    break;
                case "getCurrentWindowId":
                    return pageMethods.getCurrentWindowId();
                case "switchToWindowId":
                    isWebStepPassed = pageMethods.switchToWindowId(args[1].toString());
                    break;
                default:
                    throw new Exception(args[0].toString() + " method not implemented yet.");
            }
        } catch (Exception ex) {
            isWebStepPassed = false;
            return reportException(webDriver, args, ex);
        }
        reportExecutionStatus(isWebStepPassed, args, webDriver);
        return isWebStepPassed;
    }

    private void reportExecutionStatus(boolean isStepPassed, Object[] args, RemoteWebDriver remoteWebDriver) {
        if (isStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(GeneralUtility.getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            TestStepReportModel testStepReportModel = new TestStepReportModel(++SetUp.stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(status, remoteWebDriver);
        }
    }

    private boolean reportException(RemoteWebDriver remoteWebDriver, Object[] args, Exception ex) {
        if (remoteWebDriver == null) {
            ReportUtility.reportFail(GeneralUtility.getMethodStyleStepName(args) + " could not be performed.");
            ReportUtility.reportExceptionDebug(ex);
        } else {
            TestStepReportModel testStepReportModel = new TestStepReportModel(++stepCounter, args, ex);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.FAIL, remoteWebDriver);
        }
        return false;
    }

    protected boolean takeMobileScreenshotWithText(String text) {
        ReportUtility.reportWithScreenshot(appiumDriver, text, ReportUtility.REPORT_STATUS.INFO, text);
        return true;
    }

    public boolean closeAppOnDevice() {
        if (appiumDriver != null)
            appiumDriver.quit();
        return true;
    }

    public boolean goBackToPreviousPageInMobile() {
        return pageMethods.navigateBack(appiumDriver);
    }

    public boolean pressHomeKey() {
        ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.HOME));
        return true;
    }

    public boolean pressBackKey() {
        ((AndroidDriver) appiumDriver).pressKey(new KeyEvent(AndroidKey.BACK));
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

}
