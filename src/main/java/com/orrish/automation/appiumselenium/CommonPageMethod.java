package com.orrish.automation.appiumselenium;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.report.UIStepReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;
import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;
import static org.openqa.selenium.support.ui.ExpectedConditions.textMatches;

public class CommonPageMethod {

    protected boolean takeScreenshotWithText(String text, RemoteWebDriver driver) {
        if (screenshotDelayInSeconds > 0) {
            waitSeconds(screenshotDelayInSeconds);
        }
        String testName = getCurrentTestName().replace(" ", "");
        String screenshotName = testName + "_Step" + ++stepCounter;
        ReportUtility.reportWithScreenshot(driver, screenshotName, ReportUtility.REPORT_STATUS.INFO, text);
        return true;
    }

    public boolean waitForElementDisplayedOrGone(WebDriver webDriver, WebDriverWait webDriverWait, By locator, boolean shouldBeDisplayed) {
        if (shouldBeDisplayed) {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } else {
            List<WebElement> elementList = webDriver.findElements(locator);
            webDriverWait.until(ExpectedConditions.invisibilityOfAllElements(elementList));
        }
        return true;
    }

    protected WebElement waitUntilOneOfTheElementsIs(WebDriver webDriver, List<By> locator, boolean enabled) {
        for (int i = 0; i < 10; ++i) {
            waitSeconds(1);
            for (By eachLocator : locator) {
                try {
                    WebElement element = webDriver.findElement(eachLocator);
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

    public boolean waitUntilElementTextContains(WebDriverWait webDriverWait, By locator, String text) {
        webDriverWait.until(textMatches(locator, Pattern.compile(".*" + text + ".*")));
        return true;
    }

    public boolean waitUntilElementTextDoesNotContain(WebDriverWait webDriverWait, By locator, String text) {
        webDriverWait.until(ExpectedConditions.not(textMatches(locator, Pattern.compile(".*" + text + ".*"))));
        return true;
    }

    public WebElement waitForAndGetElement(WebDriver webDriver, WebDriverWait webDriverWait, By byElement) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
        return webDriver.findElement(byElement);
    }

    protected boolean isElementDisplayed(RemoteWebDriver webDriver, By locator) {
        try {
            return webDriver.findElement(locator).isDisplayed();
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isElementEnabled(RemoteWebDriver webDriver, WebDriverWait webDriverWait, By locator) {
        try {
            return waitForAndGetElement(webDriver, webDriverWait, locator).isEnabled();
        } catch (Exception ex) {
            return false;
        }
    }

    protected boolean isElementSelected(RemoteWebDriver webDriver, WebDriverWait webDriverWait, By locator) {
        try {
            return waitForAndGetElement(webDriver, webDriverWait, locator).isSelected();
        } catch (Exception ex) {
            return false;
        }
    }

    public By getElementBy(String locator) {
        locator = deduceDescriptiveElementLocatorValue(locator);
        if (locator.startsWith("/"))
            return By.xpath(locator);
        if (locator.startsWith("#"))
            return By.id(locator.replaceFirst("#", ""));
        if (locator.startsWith("."))
            return By.className(locator.replaceFirst(".", ""));
        if (locator.startsWith("name="))
            return By.name(locator.replace("name=", ""));
        if (locator.contains("[") && locator.contains("]"))
            return By.cssSelector(locator);

        List<String> htmlTags = Arrays.asList(new String[]{"a", "input", "img", "button", "div", "span", "p"});
        if (htmlTags.contains(locator))
            return By.tagName(locator);

        String valueToFind = locator.replace("text=", "");
        return By.xpath("//*[text()='" + valueToFind + "']");
        //return By.xpath("//*[contains (text(), '" + valueToFind + "')]");
    }

    private static String deduceDescriptiveElementLocatorValue(String locator) {
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

    public WebElement findFirstElementDisplayed(WebDriver webDriver, List<By> locators) throws Exception {
        //Trying 10 times before giving up.
        for (int i = 0; i < 10; i++) {
            for (By eachBy : locators) {
                try {
                    WebElement webElement = webDriver.findElement(eachBy);
                    if (webElement.isDisplayed())
                        return webElement;
                } catch (Exception ex) {
                }
            }
        }
        throw new Exception("Could not find any of the elements provided.");
    }

    //Appium throws org.openqa.selenium.InvalidSelectorException: Locator Strategy 'relative' is not supported for this session
    //Keeping it Common page for now in case it is available in future for Appium.
    protected WebElement findElementToTheOf(RemoteWebDriver webDriver, By finalElementBy, String direction, By pivotElementBy) {
        By locatorToFind = null;
        if (direction.contains("above"))
            locatorToFind = RelativeLocator.with(finalElementBy).above(pivotElementBy);
        if (direction.contains("below"))
            locatorToFind = RelativeLocator.with(finalElementBy).below(pivotElementBy);
        if (direction.contains("right"))
            locatorToFind = RelativeLocator.with(finalElementBy).toRightOf(pivotElementBy);
        if (direction.contains("left"))
            locatorToFind = RelativeLocator.with(finalElementBy).toLeftOf(pivotElementBy);
        return webDriver.findElement(locatorToFind);
    }

    public void reportExecutionStatusWithScreenshotAndException(boolean isStepPassed, Object[] args, RemoteWebDriver remoteWebDriver, Throwable ex) {
        if (isStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            UIStepReporter UIStepReporter = new UIStepReporter(++SetUp.stepCounter, args, ex);
            UIStepReporter.reportStepResultWithScreenshotAndException(status, remoteWebDriver);
        }
    }

}
