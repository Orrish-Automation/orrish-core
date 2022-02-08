package com.orrish.automation.appiumselenium;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.UIStepReporter;
import com.orrish.automation.utility.report.ReportUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.orrish.automation.entrypoint.GeneralSteps.getMethodStyleStepName;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.textMatches;

public class CommonPageMethod {

    protected static boolean takeScreenshotWithText(String text, RemoteWebDriver driver) {
        if (isScreenshotAtEachStepEnabled) {
            if (screenshotDelayInSeconds > 0) {
                waitSeconds(screenshotDelayInSeconds);
            }
            String testName = getCurrentTestName().replace(" ", "");
            String screenshotName = testName + "_Step" + ++stepCounter;
            ReportUtility.reportWithScreenshot(driver, screenshotName, ReportUtility.REPORT_STATUS.INFO, text);
        }
        return true;
    }

    public static boolean waitForElementSync(WebDriver webDriver, WebDriverWait webDriverWait, String locator, boolean shouldBeDisplayed) {
        List<String> locators = Arrays.asList(locator.split(",,"));
        locators.removeIf(e -> e.trim().length() == 0);
        for (String eachLocator : locators) {
            if (shouldBeDisplayed) {
                webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(getElementBy(eachLocator.trim())));
            } else {
                List<WebElement> elementList = webDriver.findElements(getElementBy(eachLocator.trim()));
                webDriverWait.until(ExpectedConditions.invisibilityOfAllElements(elementList));
            }
        }
        return true;
    }

    public static WebElement waitUntilOneOfTheLocatorsIs(WebDriver webDriver, String locator, boolean enabled) {
        String[] locatorList = locator.split(",,");
        for (int i = 0; i < 10; ++i) {
            waitSeconds(1);
            for (String eachLocator : locatorList) {
                try {
                    WebElement element = webDriver.findElement(getElementBy(eachLocator));
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

    public static boolean waitUntilElementTextContains(WebDriverWait webDriverWait, String locator, String text) {
        webDriverWait.until(textMatches(getElementBy(locator), Pattern.compile(".*" + text + ".*")));
        return true;
    }

    public static boolean waitUntilElementTextDoesNotContain(WebDriverWait webDriverWait, String locator, String text) {
        webDriverWait.until(ExpectedConditions.not(textMatches(getElementBy(locator), Pattern.compile(".*" + text + ".*"))));
        return true;
    }

    public static WebElement waitForAndGetElement(WebDriver webDriver, WebDriverWait webDriverWait, By byElement) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
        return webDriver.findElement(byElement);
    }

    public static By getElementBy(String locator) {
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

    public static String deduceDescriptiveElementLocatorValue(String locator) {
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

    public static boolean clickWithText(WebDriver webDriver, WebDriverWait webDriverWait, String locator, String text) {
        //If locator has comma separated multiple values
        if (locator.contains(",")) {
            String[] locators = locator.split(",,");
            for (String eachLocator : locators) {
                WebElement targetElement = getElement(webDriver, eachLocator, text);
                if (targetElement != null) {
                    targetElement.click();
                    return true;
                }
            }
        } else {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(getElementBy(locator)));
            getElement(webDriver, locator, text).click();
            return true;
        }
        return false;
    }

    private static WebElement getElement(WebDriver webDriver, String locator, String text) {
        List<WebElement> webElementList = webDriver.findElements(getElementBy(locator));
        return getTargetElementWithText(webElementList, text, locator);
    }

    public static WebElement getTargetElementWithText(List<WebElement> elements, String text, String locator) {
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

    public static WebElement findFirstElementDisplayed(WebDriver webDriver, String locator) throws Exception {
        String[] elementLocators = locator.split(",,");
        //Trying 10 times before giving up.
        for (int i = 0; i < 10; i++) {
            for (String eachLocator : elementLocators) {
                try {
                    return webDriver.findElement(getElementBy(eachLocator.trim()));
                } catch (Exception ex) {
                }
            }
        }
        throw new Exception("Could not find any of the elements from " + locator);
    }

    public static boolean enterInTextField(WebDriver webDriver, WebDriverWait webDriverWait, String input, String locator) {
        try {
            //Sometimes, there may be more than one element with similar locator and first one may be there but not actionable
            WebElement webElement = waitForAndGetElement(webDriver, webDriverWait, getElementBy(locator));
            webElement.clear();
            webElement.sendKeys(input);
            return true;
        } catch (Exception ex) {
            List<WebElement> elements = webDriver.findElements(getElementBy(locator));
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

    public static void reportExecutionStatus(boolean isStepPassed, Object[] args, RemoteWebDriver remoteWebDriver) {
        if (isStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            UIStepReporter UIStepReporter = new UIStepReporter(++SetUp.stepCounter, args, null);
            UIStepReporter.reportStepResultWithScreenshot(status, remoteWebDriver);
        }
    }

    public static boolean reportException(RemoteWebDriver remoteWebDriver, Object[] args, Throwable ex) {
        if (remoteWebDriver == null) {
            ReportUtility.reportFail(getMethodStyleStepName(args) + " could not be performed.");
            ReportUtility.reportExceptionDebug(ex);
        } else {
            UIStepReporter UIStepReporter = new UIStepReporter(++stepCounter, args, ex);
            UIStepReporter.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.FAIL, remoteWebDriver);
        }
        return false;
    }

}
