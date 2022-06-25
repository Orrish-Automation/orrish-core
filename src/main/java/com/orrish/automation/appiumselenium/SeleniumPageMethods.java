package com.orrish.automation.appiumselenium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.orrish.automation.entrypoint.GeneralSteps;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;

public class SeleniumPageMethods extends CommonPageMethod {

    protected RemoteWebDriver webDriver;
    protected WebDriverWait webDriverWait;
    protected WebElement lastLocatorInteractedWith;


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
        desiredCapabilities.setCapability("unhandledPromptBehavior", "ignore");

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
        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(defaultWaitTime));
        return true;
    }

    public boolean inBrowserNavigateTo(String url) {
        webDriver.navigate().to(url);
        webDriver.manage().window().maximize();
        return true;
    }

    public boolean inBrowserNavigateBack() {
        webDriver.navigate().back();
        return true;
    }

    public boolean refreshWebPage() {
        webDriver.navigate().refresh();
        return true;
    }

    public boolean switchToNewTab() throws Exception {
        Set<String> allTabs = webDriver.getWindowHandles();
        for (int i = 0; webDriver.getWindowHandles().size() == 1 && i < defaultWaitTime; i++) {
            GeneralSteps.waitSeconds(1);
            allTabs = webDriver.getWindowHandles();
        }
        String currentTab = webDriver.getWindowHandle();
        for (String tab : allTabs) {
            if (!tab.equals(currentTab)) {
                webDriver.switchTo().window(tab);
                return true;
            }
        }
        throw new Exception("Could not switch to new tab. Number of tab/window is : " + allTabs.size());
    }

    public boolean closeCurrentTab() {
        Set<String> allTabs = webDriver.getWindowHandles();
        String currentTab = webDriver.getWindowHandle();
        String tabToSwitchTo = null;
        for (String tab : allTabs) {
            if (tab.equals(currentTab)) {
                webDriver.close();
            } else {
                if (tabToSwitchTo == null)
                    tabToSwitchTo = tab;
            }
        }
        webDriver.switchTo().window(tabToSwitchTo);
        return true;
    }

    public boolean takeWebScreenshotWithText(String text) {
        takeScreenshotWithText(text, webDriver);
        return true;
    }

    public boolean executeJavascript(String scriptToExecute) {
        webDriver.executeScript(scriptToExecute);
        return true;
    }

    public boolean checkAccessibilityForPage(String pageTitle) throws IOException {
        pageTitle = pageTitle.trim().length() == 0 ? webDriver.getCurrentUrl() : pageTitle;
        ArrayNode violations = getAccessibilityViolationsForCurrentPage();
        if (violations.size() == 0) {
            ReportUtility.reportPass(pageTitle + " page accessibility check passed.");
        } else {
            final String[] failureMessage = {""};
            violations.forEach(e -> failureMessage[0] += e.toString());
            List<String> violationsCategory = new ArrayList<>();
            violations.forEach(e -> violationsCategory.add(e.get("id").textValue()));
            ReportUtility.reportFail("\"" + pageTitle + "\" page accessibility check failed with " + violations.size() + " violations: " + violationsCategory);
            //TODO: Below line will mess up the report if the webpage contains any json
            ReportUtility.reportJsonAsInfo("Violations:", failureMessage[0]);
        }
        return violations.size() == 0;
    }

    private ArrayNode getAccessibilityViolationsForCurrentPage() throws IOException {
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

    public Object getPageUrl() {
        return webDriver.getCurrentUrl();
    }

    public void scrollTo(WebElement element) {
        webDriver.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public boolean waitUntilIsDisplayed(String locator) {
        return waitForElementDisplayedOrGone(webDriver, webDriverWait, getElementBy(locator), true);
    }

    public boolean waitUntilIsGone(String locator) {
        return waitForElementDisplayedOrGone(webDriver, webDriverWait, getElementBy(locator), false);
    }

    public boolean waitUntilOneOfIsDisplayed(String locator) {
        List<By> by = getAllByFromLocator(locator);
        WebElement webElement = waitUntilOneOfTheElementsIs(webDriver, by, false);
        return webElement != null;
    }

    public boolean waitUntilOneOfIsEnabled(String locator) {
        List<By> by = getAllByFromLocator(locator);
        WebElement webElement = waitUntilOneOfTheElementsIs(webDriver, by, true);
        return webElement != null;
    }

    private List<By> getAllByFromLocator(String locator) {
        String[] elementLocators = locator.split(",,");
        List<By> by = new ArrayList<>();
        for (String elementLocator : elementLocators) {
            by.add(getElementBy(elementLocator));
        }
        return by;
    }

    public boolean waitUntilContains(String locator, String text) {
        waitUntilElementTextContains(webDriverWait, getElementBy(locator), text);
        return true;
    }

    public boolean waitUntilDoesNotContain(String locator, String text) {
        waitUntilElementTextDoesNotContain(webDriverWait, getElementBy(locator), text);
        return true;
    }

    public boolean pressKey(String keyToPress) {
        if (keyToPress.equalsIgnoreCase("escape"))
            webDriver.findElement(By.xpath("//*")).sendKeys(Keys.ESCAPE);
        if (keyToPress.equalsIgnoreCase("space"))
            webDriver.findElement(By.xpath("//*")).sendKeys(Keys.SPACE);
        if (keyToPress.equalsIgnoreCase("backspace"))
            webDriver.findElement(By.xpath("//*")).sendKeys(Keys.BACK_SPACE);
        return true;
    }

    public boolean rightClick(String locator) {
        WebElement webElement = webDriver.findElement(getElementBy(locator));
        Actions actions = new Actions(webDriver);
        actions.contextClick(webElement).perform();
        return true;
    }

    public boolean isDisplayed(String locator) {
        return isElementDisplayed(webDriver, getElementBy(locator));
    }

    public boolean isEnabled(String locator) {
        return isElementEnabled(webDriver, webDriverWait, getElementBy(locator));
    }

    public boolean isSelected(String locator) {
        return isElementSelected(webDriver, webDriverWait, getElementBy(locator));
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
        return waitForAndGetElement(webDriver, webDriverWait, byElement);
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

    public boolean clickColumnWhere(String headerText, String value) throws Exception {
        getTargetTableCell(headerText, value).click();
        return true;
    }

    public String getColumnWhere(String headerText, String value) throws Exception {
        return getTargetTableCell(headerText, value).getText();
    }

    public boolean clickInColumnWhere(String textToClick, String columnToFind, String value) throws Exception {
        WebElement cell = getTargetTableCell(columnToFind, value);
        cell.findElement(By.xpath("./child::*[text()='" + textToClick + "']")).click();
        return true;
    }

    private WebElement getTargetTableCell(String columnToGet, String valueToFind) throws Exception {

        //TODO: It does not work when the column header name has parantheses
        //This is to accommodate case where column name contains equal sign
        int lastIndexOfEqualSign = valueToFind.lastIndexOf("=");
        String columnToLocate = valueToFind.substring(0, lastIndexOfEqualSign);
        String valueToLocate = valueToFind.substring(lastIndexOfEqualSign + 1);

        List<WebElement> tables = webDriver.findElements(By.tagName("table"));
        tables.addAll(webDriver.findElements(By.cssSelector("[role=grid]")));

        int columnIndexToFind = -1;
        int columnIndexToGet = -1;

        for (WebElement table : tables) {
            List<WebElement> headers = table.findElements(By.tagName("th"));
            headers.addAll(table.findElements(By.cssSelector("[role=columnHeader]")));
            headers.addAll(table.findElements(By.cssSelector("[role=columnheader]")));
            for (int j = 0; j < headers.size(); j++) {
                String headerText = headers.get(j).getText();
                if (headerText.trim().equals(columnToLocate)) {
                    columnIndexToFind = j;
                }
                if (headerText.trim().equals(columnToGet)) {
                    columnIndexToGet = j;
                }
                if (columnIndexToFind != -1 && columnIndexToGet != -1) {
                    List<WebElement> tbody = table.findElements(By.tagName("tbody"));
                    List<WebElement> rows = new ArrayList<>();
                    if (tbody.size() > 0) {
                        rows = tbody.get(0).findElements(By.tagName("tr"));
                    } else {
                        rows = webDriver.findElements(By.cssSelector("[role=row]"));
                    }
                    for (WebElement eachRow : rows) {
                        List<WebElement> cells = eachRow.findElements(By.cssSelector("[role=cell]"));
                        cells.addAll(webDriver.findElements(By.cssSelector("[role=gridcell]")));
                        if (cells.size() == 0) {
                            cells = eachRow.findElements(By.cssSelector("th"));
                            cells.addAll(eachRow.findElements(By.cssSelector("td")));
                        }
                        if (cells.get(columnIndexToFind).getText().equals(valueToLocate)) {
                            return cells.get(columnIndexToGet);
                        }
                    }
                }
            }
        }
        throw new Exception("Did not get any table with the criteria: " + valueToFind + " & existence of column" + columnToGet);
    }

    public boolean uploadFile(String filePath) throws Exception {
        List<WebElement> fileButtons = webDriver.findElements(By.cssSelector("input[type='file']"));
        if (fileButtons.size() == 0)
            throw new Exception("Could not find any button of type file.");
        fileButtons.get(0).sendKeys(filePath);
        return true;
    }

    public boolean clickWithPartialText(String text) {
        By by = By.xpath("//*[ contains (text(), '" + text + "')]");
        waitForAndGetElement(by);
        List<WebElement> elementList = webDriver.findElements(by);
        if (elementList.size() > 0) {
            ReportUtility.reportInfo("Clicking the first element of " + elementList.size() + " elements found with partial text with value :" + text);
        }
        elementList.get(0).click();
        return true;
    }

    private boolean isLocatorPlainText(String text) {
        By pivotElementBy = getElementBy(text);
        return pivotElementBy.toString().startsWith("By.xpath: //*[text()=");
    }

    public String getTextFromToTheOf(String textToClick, String direction, String pivotElementText) {
        By pivotElementBy = isLocatorPlainText(pivotElementText) ? By.xpath("//*[ contains (text(), '" + pivotElementText + "')]") : getElementBy(pivotElementText);
        By finalElementBy = isLocatorPlainText(textToClick) ? By.xpath("//*[ contains (text(), '" + textToClick + "')]") : getElementBy(textToClick);
        return findElementToTheOf(webDriver, finalElementBy, direction, pivotElementBy).getText();
    }

    public boolean clickToTheOf(String textToClick, String direction, String pivotElementText) {
        By pivotElementBy = isLocatorPlainText(pivotElementText) ? By.xpath("//*[ contains (text(), '" + pivotElementText + "')]") : getElementBy(pivotElementText);
        By finalElementBy = isLocatorPlainText(textToClick) ? By.xpath("//*[ contains (text(), '" + textToClick + "')]") : getElementBy(textToClick);
        findElementToTheOf(webDriver, finalElementBy, direction, pivotElementBy).click();
        return true;
    }

    public boolean click(String text) {
        return clickNumber(text, "1");
    }

    public boolean clickNumber(String text, String number) {
        List<WebElement> elementList = webDriver.findElements(getElementBy(text));
        if (elementList.size() == 0) {
            elementList.addAll(webDriver.findElements(By.cssSelector("[value='" + text + "']")));
        }
        lastLocatorInteractedWith = elementList.get(Integer.parseInt(number) - 1);
        lastLocatorInteractedWith.click();
        return true;
    }

    public boolean hoverOn(String text) {
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webDriver.findElement(getElementBy(text))).perform();
        return true;
    }

    public boolean clickIcon(String textToClick) throws Exception {
        WebElement icon = (WebElement) getIconCorrespondingTo(textToClick, "svg", "element");
        icon = (icon == null) ? (WebElement) getIconCorrespondingTo(textToClick, "a", "element") : icon;
        icon = (icon == null) ? (WebElement) getIconCorrespondingTo(textToClick, "img", "element") : icon;
        icon = (icon == null) ? (WebElement) getIconCorrespondingTo(textToClick, "button", "element") : icon;

        if (icon == null) {
            throw new Exception("Could not find icon with the given criteria.");
        }
        icon.click();
        return true;
    }

    public boolean clickIconNextTo(String iconToClick, String pivotText) throws Exception {
        By icon = (By) getIconCorrespondingTo(iconToClick, "svg", "by");
        icon = (icon == null) ? (By) getIconCorrespondingTo(iconToClick, "a", "by") : icon;
        icon = (icon == null) ? (By) getIconCorrespondingTo(iconToClick, "img", "by") : icon;
        icon = (icon == null) ? (By) getIconCorrespondingTo(iconToClick, "button", "by") : icon;
        List<WebElement> webElementList = webDriver.findElements(RelativeLocator.with(icon).toRightOf(getElementBy(pivotText)));
        if (webElementList.size() == 0)
            webElementList.addAll(webDriver.findElements(RelativeLocator.with(icon).toRightOf(getElementBy(pivotText))));
        if (webElementList.size() == 0)
            throw new Exception("Could not find any icon next to " + pivotText);
        webElementList.get(0).click();
        return true;
    }

    private Object getIconCorrespondingTo(String iconToClick, String locatorText, String elementOrBy) {
        By by = By.tagName(locatorText);
        List<WebElement> webElements = webDriver.findElements(by);
        for (WebElement eachLocator : webElements) {
            String attributeAria = String.valueOf(eachLocator.getAttribute("aria-label"));
            String attributeAlt = String.valueOf(eachLocator.getAttribute("alt"));
            String attributeTitle = String.valueOf(eachLocator.getAttribute("title"));
            String combinedAlt = attributeAlt + attributeAria + attributeTitle;

            if (combinedAlt.contains(iconToClick))
                return elementOrBy.contains("element") ? eachLocator : by;
            WebElement parent = eachLocator.findElement(By.xpath(".."));
            String hrefProperty = String.valueOf(parent.getAttribute("href"));
            String idProperty = String.valueOf(parent.getAttribute("id"));
            String classProperty = String.valueOf(parent.getAttribute("class"));
            if (hrefProperty.contains(iconToClick) || idProperty.contains(iconToClick) || classProperty.contains(iconToClick))
                return elementOrBy.contains("element") ? eachLocator : by;
        }
        return null;
    }

    public boolean clickWhicheverIsDisplayedIn(String locator) throws Exception {
        List<By> by = getAllByFromLocator(locator);
        WebElement webElement = findFirstElementDisplayed(webDriver, by);
        webElement.click();
        return true;
    }

    public boolean typeIn(String input, String locator) throws Exception {
        //Sometimes, there may be more than one element with similar locator and first one may be there but not actionable
        waitForAndGetElement(getElementBy(locator));
        List<WebElement> elementList = webDriver.findElements(getElementBy(locator));
        for (WebElement element : elementList) {
            while (!element.getTagName().equalsIgnoreCase("input") && element.findElements(By.tagName("input")).size() == 0) {
                element = element.findElement(By.xpath(".."));
            }
            if (element.getTagName().equalsIgnoreCase("input")) {
                element.sendKeys(input);
            } else {
                element.findElement(By.tagName("input")).sendKeys(input);
            }
            return true;
        }
        throw new Exception("Could not find element with locator : " + locator);
    }

    public boolean typeInNumber(String textToEnter, String indexNumber) {
        int index = Integer.parseInt(indexNumber);
        List<WebElement> inputElements = getFilteredWebElements();
        inputElements.removeIf(e -> !"text or password or number".contains(e.getAttribute("type")));
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

    public boolean typeWithPartialText(String input, String locator) throws Exception {
        return typeIn(input, "//*[contains(text(),'" + locator + "')]");
    }

    public WebElement getElementFromFrames(By by) throws Exception {
        List<WebElement> iframes = webDriver.findElements(By.tagName("iframe"));
        for (WebElement iframe : iframes) {
            webDriver.switchTo().frame(iframe);
            List<WebElement> elementList = webDriver.findElements(by);
            if (elementList.size() > 0) {
                return elementList.get(0);
            }
        }
        throw new Exception("Did not find an element with the criteria : " + by.toString());
    }

    public String getFullTextFor(String value) {
        By by = isLocatorPlainText(value) ? By.xpath("(//*[contains(text(),'" + value + "')])") : getElementBy(value);
        return waitForAndGetElement(by).getText();
    }

    //method to verify string is present in the webpage or not
    public boolean checkTextIsPresentInWebpage(String value) {
        waitForAndGetElement(By.xpath("(//*[contains(text(),'" + value + "')])")).isDisplayed();
        return true;
    }

    public boolean selectFromDropdown(String inputString, String locator) throws Exception {
        List<WebElement> selects = webDriver.findElements(By.tagName("select"));
        for (WebElement eachSelect : selects) {
            List<WebElement> options = eachSelect.findElements(getElementBy(locator));
            if (options.size() > 0) {
                new Select(eachSelect).selectByVisibleText(inputString);
                return true;
            }
        }
        throw new Exception("Could not find the locator : " + locator);
    }

    public boolean clearText() {
        lastLocatorInteractedWith.clear();
        return true;
    }

    private WebElement getTargetInputElement(String stringToFind, String typeOfInput) {
        List<WebElement> targetLocators = webDriver.findElements(getElementBy("input[type=" + typeOfInput + "]"));
        for (WebElement eachLocator : targetLocators) {
            WebElement originalLocator = eachLocator;
            while (eachLocator.getText().trim().length() == 0)
                eachLocator = eachLocator.findElement(By.xpath(".."));
            if (eachLocator.isDisplayed() && eachLocator.getText().trim().equals(stringToFind.trim()))
                return originalLocator;
        }
        return null;
    }

    public boolean selectRadioForText(String text) throws Exception {
        WebElement webElement = getTargetInputElement(text, "radio");
        if (webElement == null)
            throw new Exception("Could not find an element with text : " + text);
        webElement.click();
        return true;
    }

    private boolean selectUnselectcheckboxeswithText(String stringToFind, boolean shouldBeSelected) throws Exception {
        List<String> textsToActOn = Arrays.asList(stringToFind.split(",,"));
        WebElement finalLocatorToClick = null;
        for (String eachText : textsToActOn) {
            finalLocatorToClick = getTargetInputElement(eachText, "checkbox");
            if (finalLocatorToClick == null)
                throw new Exception("Could not find checkbox corresponding to : " + stringToFind);

            if ((finalLocatorToClick.isSelected() && !shouldBeSelected) || (!finalLocatorToClick.isSelected() && shouldBeSelected))
                finalLocatorToClick.click();
        }
        return true;
    }

    public boolean selectCheckboxForText(String stringToFind) throws Exception {
        return selectUnselectcheckboxeswithText(stringToFind, true);
    }

    public boolean unselectCheckboxForText(String stringToFind) throws Exception {
        return selectUnselectcheckboxeswithText(stringToFind, true);
    }

    protected List<WebElement> getFilteredWebElements() {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));
        return webDriver.findElements(By.tagName("input"));
    }

    public boolean maximizeTheWindow() {
        webDriver.manage().window().maximize();
        return true;
    }

    public boolean scrollTo(String locator) {
        webDriver.executeScript("arguments[0].scrollIntoView(true);", webDriver.findElement(getElementBy(locator)));
        return true;
    }

    public void reportExecutionStatusWithScreenshotAndException(boolean isStepPassed, Object[] args, Throwable ex) {
        reportExecutionStatusWithScreenshotAndException(isStepPassed, args, webDriver, ex);
    }

}
