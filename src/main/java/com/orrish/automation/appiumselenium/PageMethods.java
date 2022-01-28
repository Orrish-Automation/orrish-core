package com.orrish.automation.appiumselenium;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.orrish.automation.entrypoint.SetUp.defaultWaitTime;
import static com.orrish.automation.utility.GeneralUtility.waitSeconds;
import static org.openqa.selenium.support.ui.ExpectedConditions.textMatches;

public class PageMethods {

    protected final RemoteWebDriver webDriver;
    protected final AppiumDriver appiumDriver;
    protected WebDriverWait webDriverWait;
    protected WebDriverWait appiumDriverWait;

    public PageMethods() {
        webDriver = SetUp.webDriver;
        appiumDriver = SetUp.appiumDriver;
        if (webDriver != null)
            webDriverWait = new WebDriverWait(webDriver, defaultWaitTime);
        if (appiumDriver != null)
            appiumDriverWait = new WebDriverWait(appiumDriver, defaultWaitTime);
    }

    public boolean navigateBack(WebDriver driver) {
        driver.navigate().back();
        ReportUtility.reportInfo("Navigated backward in page.");
        return true;
    }

    public boolean refreshPage() {
        webDriver.navigate().refresh();
        ReportUtility.reportInfo("Refreshed page.");
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

    private boolean waitForElementSync(WebDriver driver, String locator, boolean shouldBeDisplayed) {
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
            return SetUp.appiumDriver.findElement(byElement);
        }
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(byElement));
        return SetUp.webDriver.findElement(byElement);
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
