package com.orrish.automation.playwright;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitUntilState;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.report.UIStepReporter;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;
import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;

public class PlaywrightActions extends ElementActions {

    private static PlaywrightActions playwrightActions;

    public static PlaywrightActions getInstance() {
        if (playwrightActions == null) {
            synchronized (PlaywrightActions.class) {
                if (playwrightActions == null) {
                    playwrightActions = new PlaywrightActions();
                }
            }
        }
        return playwrightActions;
    }

    public boolean isPlaywrightRunning() {
        return playwrightPage != null;
    }

    public Page getPlaywrightPage() {
        return playwrightPage;
    }

    public boolean quitPlaywright() {
        try {
            if (playwrightPage != null && !playwrightPage.isClosed()) {
                playwrightPage.close();
                savePlaywrightVideoIfEnabled();
            }
            if (playwright != null) {
                playwright.close();
            }
        } catch (Exception ex) {
            ReportUtility.reportExceptionDebug(ex);
        }
        return true;
    }

    protected boolean launchBrowserAndNavigateTo(String url) {

        playwright = Playwright.create();
        Browser browser;
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(isPlaywrightHeadless);

        switch (SetUp.browser.trim().toUpperCase()) {
            case "CHROME":
                browser = playwright.chromium().launch(launchOptions);
                break;
            case "FIREFOX":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "SAFARI":
                browser = playwright.webkit().launch(launchOptions);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + SetUp.browser.trim().toUpperCase());
        }
        Browser.NewContextOptions browserContext = new Browser.NewContextOptions().setIgnoreHTTPSErrors(true);
        if (isVideoRecordingEnabled()) {
            browserContext.setRecordVideoDir(Paths.get("videos/"));
        }
        BrowserContext context = browser.newContext(browserContext);
        playwrightPage = context.newPage();
        playwrightPage.setDefaultNavigationTimeout(playwrightDefaultNavigationWaitTimeInSeconds * 1000);
        playwrightPage.setDefaultTimeout(defaultWaitTime * 1000);
        playwrightPage.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
        return true;
    }

    protected boolean navigateTo(String url) {
        playwrightPage.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
        return true;
    }

    protected boolean maximizeTheWindow() throws Exception {
        throw new Exception("Not implemented. Track issue at https://github.com/microsoft/playwright/issues/4046");
    }

    protected boolean inBrowserNavigateBack() {
        playwrightPage.goBack();
        return true;
    }

    protected boolean refreshWebPage() {
        playwrightPage.reload();
        return true;
    }

    protected boolean takeWebScreenshotWithText(String text) {
        ReportUtility.reportWithScreenshot(null, text, ReportUtility.REPORT_STATUS.INFO, text);
        return true;
    }

    protected boolean saveAsPdfWithName(String name) {
        playwrightPage.pdf(new Page.PdfOptions().setPath(Paths.get(name)));
        return true;
    }

    protected ArrayNode checkAccessibilityForPage() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URL url = new URL("https://cdnjs.cloudflare.com/ajax/libs/axe-core/3.5.5/axe.min.js");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = in.readLine()) != null)
                stringBuilder.append(line);
        }
        executeJavascript(stringBuilder.toString());
        String results = String.valueOf(playwrightPage.evaluate("var callback = e => e; axe.run().then(results => callback(JSON.stringify(results.violations)));"));
        ArrayNode arrayNode = new ObjectMapper().readValue(results, ArrayNode.class);
        return arrayNode;
    }

    protected boolean clickExactly(String text) throws Exception {
        return clickElement(text, true);
    }

    protected boolean click(String text) throws Exception {
        return clickElement(text, false);
    }

    private boolean clickElement(String text, boolean isExact) throws Exception {
        waitUntilTextIsDisplayed(text);
        ElementHandle elementHandle = null;
        try {
            elementHandle = isExact ? getElementWithExactText(text) : getContainingText(text);
            elementHandle.click();
        } catch (TimeoutError ex) {
            //Force click it per comment in https://github.com/microsoft/playwright/issues/12298#issuecomment-1051261068
            elementHandle.click(new ElementHandle.ClickOptions().setForce(true));
        }
        playwrightPage.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return true;
    }

    protected boolean clickAndclearText(String text) throws Exception {
        waitUntilTextIsDisplayed(text);
        ElementHandle elementHandle = getContainingText(text);
        elementHandle.click();
        elementHandle.fill("");
        return true;
    }

    protected boolean clickHtmlTagWithText(String locator, String text) {
        locator = Arrays.asList(new String[]{"textbox", "text box", "input box", "inputbox"}).equals(locator.trim().toLowerCase()) ? "input" : locator;
        locator = Arrays.asList(new String[]{"link"}).equals(locator.trim().toLowerCase()) ? "a" : locator;
        locator = Arrays.asList(new String[]{"image"}).equals(locator.trim().toLowerCase()) ? "img" : locator;
        playwrightPage.locator(locator + ":has-text(\"" + text + "\")").click();
        return true;
    }

    protected boolean clickIcon(String textToClick) throws Exception {

        List<ElementHandle> icons = getIconsCorrespondingTo(textToClick, "svg");
        icons = (icons.size() == 0) ? getIconsCorrespondingTo(textToClick, "img") : icons;
        icons = (icons.size() == 0) ? getIconsCorrespondingTo(textToClick, "button") : icons;

        if (icons.size() == 0) {
            throw new Exception("Could not find icon with the given criteria.");
        }
        icons.get(0).click();
        return true;
    }

    protected List<ElementHandle> getRelativeImages(String iconTextToClick, String direction, String textToFind) throws Exception {
        //svg:right-of(:text("Home"))
        String relativeLocatorString = ":" + getDirection(direction) + "(:text(\"" + textToFind + "\"))";
        List<ElementHandle> icons = getIconsCorrespondingTo(iconTextToClick, "svg" + relativeLocatorString);
        icons = (icons.size() == 0) ? getIconsCorrespondingTo(iconTextToClick, "img" + relativeLocatorString) : icons;
        icons = (icons.size() == 0) ? getIconsCorrespondingTo(iconTextToClick, "button" + relativeLocatorString) : icons;
        icons = (icons.size() == 0) ? getIconsCorrespondingTo(iconTextToClick, "a" + relativeLocatorString) : icons;
        return icons;
    }

    protected boolean clickToTheOf(String textToClick, String direction, String textToFind) {
        getRelativeTextElement(textToClick, direction, textToFind).click();
        return true;
    }

    protected boolean clickToTheOfAndClearText(String textToClick, String direction, String textToFind) {
        getRelativeTextElement(textToClick, direction, textToFind).fill("");
        return true;
    }

    //TODO: Redefine this.
    protected String getTextFromToTheOf(String textToClick, String direction, String textToFind) {
        return getRelativeTextElement(textToClick, direction, textToFind).textContent();
    }

    protected boolean clickIconNextTo(String iconToClick, String textToFind) throws Exception {
        List<ElementHandle> icons = getRelativeImages(iconToClick, "right", textToFind);
        icons = icons.size() == 0 ? getRelativeImages(iconToClick, "left", textToFind) : icons;
        if (icons.size() == 0) {
            throw new Exception("Could not find icon with the given criteria.");
        }
        icons.get(0).click();
        return true;
    }


    protected boolean clickWhicheverIsDisplayedIn(String locator) throws Exception {
        //page.locator("button:has-text(\"Log in\"), button:has-text(\"Sign in\")").click();
        String[] eachParts = locator.split(",,");
        for (String eachPart : eachParts) {
            try {
                eachPart = isLocatorPlainText(eachPart) ? "text=" + eachPart : eachPart;
                Locator probableLocator = playwrightPage.locator(eachPart);
                if (probableLocator.count() == 0)
                    continue;
                probableLocator.click();
                return true;
            } catch (Exception ex) {
            }
        }
        throw new Exception("Could not find any element with the selected criteria. " + locator);
    }

    protected boolean type(String value) {
        playwrightPage.keyboard().type(value);
        return true;
    }

    protected boolean typeInExactly(String value, String locator) {
        waitUntilExactlyTextIsDisplayed(locator);
        List<String> inputElements = Arrays.asList(new String[]{"input", "textarea", "contenteditable"});
        if (inputElements.contains(locator))
            playwrightPage.locator(locator).first().fill(value);
        else {
            List<ElementHandle> elementHandles = getElementsWithText(locator, true);
            elementHandles.removeIf(e -> {
                String tagName = e.getProperty("tagName").jsonValue().toString();
                return !(tagName.equalsIgnoreCase("label") || tagName.equalsIgnoreCase("input"));
            });
            elementHandles.get(0).fill(value);
        }
        return true;
    }

    protected boolean typeIn(String value, String locator) {
        boolean locatorFound = false;
        for (int i = 0; i < SetUp.defaultWaitTime && !locatorFound; i++) {
            List<ElementHandle> list = playwrightPage.querySelectorAll("text=" + locator + "");
            list.addAll(playwrightPage.querySelectorAll("xpath=//input[contains(@placeholder,'" + locator + "')]"));
            for (ElementHandle each : list) {
                if (each.isVisible()) {
                    locatorFound = true;
                    break;
                }
            }
            waitSeconds(1);
        }
        List<String> inputElements = Arrays.asList(new String[]{"input", "textarea", "contenteditable"});
        if (inputElements.contains(locator))
            playwrightPage.locator(locator).first().fill(value);
        else {
            //List<ElementHandle> elementHandles = playwrightPage.querySelectorAll("xpath=//label[contains(.,'" + value + "')]");
            List<ElementHandle> elementHandles = getElementsWithText(locator, false);
            elementHandles.removeIf(e -> {
                String tagName = e.getProperty("tagName").jsonValue().toString();
                return !(tagName.equalsIgnoreCase("label") || tagName.equalsIgnoreCase("input"));
            });
            elementHandles.get(0).fill(value);
        }
        return true;
    }

    protected boolean typeInTextFieldNumber(String text, int whichField) {
        playwrightPage.waitForSelector("input");
        List<ElementHandle> elementHandleList = playwrightPage.querySelectorAll("input");
        elementHandleList.get(whichField - 1).fill(text); //Convert to zero based index.
        return true;
    }

    protected boolean selectRadioForText(String text) throws Exception {
        List<ElementHandle> radioHandles = playwrightPage.querySelectorAll("[type=radio]:left-of(:text(\"" + text + "\"))");
        radioHandles.removeIf(e -> !e.isVisible());
        if (radioHandles.size() == 0) {
            radioHandles = playwrightPage.querySelectorAll("xpath=//label[contains(.,'" + text + "')]");
            radioHandles.removeIf(e -> !e.isVisible());
        }
        if (radioHandles.size() == 0)
            throw new Exception("Could not find a radio button with text " + text);
        try {
            radioHandles.get(0).click();
        } catch (TimeoutError ex) {
            radioHandles.get(0).click(new ElementHandle.ClickOptions().setForce(true));
        }
        return true;
    }

    protected boolean selectFromDropdown(String value, String locatorText) {
        Locator locator = playwrightPage.locator("text=" + locatorText);
        locator = locator.locator("xpath=..");
        locator.selectOption(new SelectOption().setLabel(value));
        return true;
    }

    protected boolean isTextPresentInWebpage(String text) {
        List<ElementHandle> allElements = playwrightPage.querySelectorAll("text=" + text);
        allElements.removeIf(e -> !e.isVisible());
        for (ElementHandle elementHandle : allElements) {
            if (elementHandle.textContent().contains(text))
                return true;
        }
        return false;
    }

    protected String clickAndReturnAlertText(String locatorText) throws Exception {
        final String[] message = new String[1];
        playwrightPage.onDialog(dialog -> {
            message[0] = dialog.message();
        });
        ElementHandle elementHandle = getElementWithExactText(locatorText);
        elementHandle.click();
        return message[0];
    }

    protected boolean clickAndAcceptAlertIfPresent(String locatorText) throws Exception {
        playwrightPage.onDialog(dialog -> {
            ReportUtility.reportInfo("Alert with text \"" + dialog.message() + "\" on clicking " + locatorText + " is accepted.");
            dialog.accept();
        });
        ElementHandle elementHandle = getContainingText(locatorText);
        elementHandle.click();
        return true;
    }

    protected boolean waitUntilElementIsGone(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            ElementHandle elementHandle = playwrightPage.querySelector(locatorText);
            if (elementHandle == null || !elementHandle.isVisible())
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilTextIsGone(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            List<ElementHandle> elementHandle = playwrightPage.querySelectorAll("text=" + locatorText);
            elementHandle.removeIf(e -> !e.isVisible());
            if (elementHandle.size() == 0)
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilOneOfTheTextsIsDisplayed(String locator) {
        List<String> list = Arrays.asList(locator.split(",,"));
        for (int i = 0; i < defaultWaitTime; i++) {
            for (String eachItem : list) {
                try {
                    Locator element = playwrightPage.locator("text=" + eachItem);
                    if (element != null && element.count() > 0) {
                        return true;
                    }
                } catch (Exception ex) {
                }
            }
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilOneOfTheElementsIsDisplayed(String locator) {
        return waitUntilOneOfTheElements(locator, "visible");
    }

    protected boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        return waitUntilOneOfTheElements(locator, "enabled");
    }

    protected String getTextFromLocator(String locator) {
        return playwrightPage.textContent(locator);
    }

    protected String getCompleteTextFor(String text) {
        return playwrightPage.textContent("text=" + text);
    }

    protected boolean executeJavascript(String jsCode) {
        playwrightPage.evaluate(jsCode);
        return true;
    }

    protected boolean executeJavascriptOnElement(String jsCode, String locator) {
        playwrightPage.evalOnSelector(locator, jsCode);
        return true;
    }

    public String executeOnWebAndReturnString(Object... args) {
        Object valueToReturn = executeOnWebAndReturnObject(args);
        if (valueToReturn == null)
            return "";
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " returned " + valueToReturn);
        return valueToReturn.toString();
    }

    public boolean executeOnWebAndReturnBoolean(Object... args) {
        Object valueToReturn = executeOnWebAndReturnObject(args);
        return valueToReturn != null && Boolean.parseBoolean(valueToReturn.toString());
    }

    protected Object executeOnWebAndReturnObject(Object... args) {
        if (!conditionalStep) return null;
        if (!isPlaywrightStepPassed) {
            ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
            return null;
        }
        try {
            switch (args[0].toString()) {
                case "launchBrowserAndNavigateTo":
                    isPlaywrightStepPassed = launchBrowserAndNavigateTo(args[1].toString());
                    break;
                case "inBrowserNavigateTo":
                    isPlaywrightStepPassed = navigateTo(args[1].toString());
                    break;
                case "maximizeTheWindow":
                    isPlaywrightStepPassed = maximizeTheWindow();
                    break;
                case "inBrowserNavigateBack":
                    isPlaywrightStepPassed = inBrowserNavigateBack();
                    break;
                case "refreshWebPage":
                    isPlaywrightStepPassed = refreshWebPage();
                    break;
                case "quitPlaywright":
                    isPlaywrightStepPassed = quitPlaywright();
                    break;
                case "takeWebScreenshotWithText":
                    return takeWebScreenshotWithText(args[1].toString());
                case "saveAsPdfWithName":
                    return saveAsPdfWithName(args[1].toString());
                case "getPageTitle":
                    return playwrightPage.title();
                case "getPageUrl":
                    return playwrightPage.url();
                case "forRequestUseMockStatusAndResponse":
                    return forRequestUseMockStatusAndResponse(args[1].toString(), Integer.parseInt(args[2].toString()), args[3].toString());
                case "checkAccessibilityForPage":
                    ArrayNode violations = getAccessibilityViolations(args);
                    return violations.size() == 0;
                case "click":
                    isPlaywrightStepPassed = click(args[1].toString());
                    break;
                case "clickExactly":
                    isPlaywrightStepPassed = clickExactly(args[1].toString());
                    break;
                case "clickAndClearText":
                    isPlaywrightStepPassed = clickAndclearText(args[1].toString());
                    break;
                case "clickHtmlTagWithText":
                    isPlaywrightStepPassed = clickHtmlTagWithText(args[1].toString(), args[2].toString());
                    break;
                case "clickIcon":
                    isPlaywrightStepPassed = clickIcon(args[1].toString());
                    break;
                case "clickIconNextTo":
                    isPlaywrightStepPassed = clickIconNextTo(args[1].toString(), args[2].toString());
                    break;
                case "clickToTheOf":
                    isPlaywrightStepPassed = clickToTheOf(args[1].toString(), args[2].toString(), args[3].toString());
                    break;
                case "clickToTheOfAndClearText":
                    isPlaywrightStepPassed = clickToTheOfAndClearText(args[1].toString(), args[2].toString(), args[3].toString());
                    break;
                case "clickWhicheverIsDisplayedIn":
                    isPlaywrightStepPassed = clickWhicheverIsDisplayedIn(args[1].toString());
                    break;
                case "type":
                    isPlaywrightStepPassed = type(args[1].toString());
                    break;
                case "typeIn":
                    isPlaywrightStepPassed = typeIn(args[1].toString(), args[2].toString());
                    break;
                case "typeInExactly":
                    isPlaywrightStepPassed = typeInExactly(args[1].toString(), args[2].toString());
                    break;
                case "typeInTextFieldNumber":
                    isPlaywrightStepPassed = typeInTextFieldNumber(args[1].toString(), Integer.parseInt(args[2].toString()));
                    break;
                case "selectCheckboxForText":
                    isPlaywrightStepPassed = selectUnselectCheckboxesWithText(args[1].toString(), true);
                    break;
                case "unselectCheckboxForText":
                    isPlaywrightStepPassed = selectUnselectCheckboxesWithText(args[1].toString(), false);
                    break;
                case "selectRadioForText":
                    isPlaywrightStepPassed = selectRadioForText(args[1].toString());
                    break;
                case "selectFromDropdown":
                    isPlaywrightStepPassed = selectFromDropdown(args[1].toString(), args[2].toString());
                    break;
                case "isTextPresentInWebpage":
                    isPlaywrightStepPassed = isTextPresentInWebpage(args[1].toString());
                    break;
                case "waitUntilElementContains":
                    isPlaywrightStepPassed = waitUntilElementContains(args[1].toString(), args[2].toString());
                    break;
                case "waitUntilTextIsDisplayed":
                    isPlaywrightStepPassed = waitUntilTextIsDisplayed(args[1].toString());
                    break;
                case "waitUntilElementDoesNotContain":
                    isPlaywrightStepPassed = waitUntilElementDoesNotContain(args[1].toString(), args[2].toString());
                    break;
                case "waitUntilElementIsGone":
                    isPlaywrightStepPassed = waitUntilElementIsGone(args[1].toString());
                    break;
                case "waitUntilTextIsGone":
                    isPlaywrightStepPassed = waitUntilTextIsGone(args[1].toString());
                    break;
                case "waitUntilElementIsDisplayed":
                    isPlaywrightStepPassed = waitUntilElementIsDisplayed(args[1].toString());
                    break;
                case "waitUntilOneOfTheTextsIsDisplayed":
                    isPlaywrightStepPassed = waitUntilOneOfTheTextsIsDisplayed(args[1].toString());
                    break;
                case "waitUntilOneOfTheElementsIsDisplayed":
                    isPlaywrightStepPassed = waitUntilOneOfTheElementsIsDisplayed(args[1].toString());
                    break;
                case "waitUntilOneOfTheElementsIsEnabled":
                    isPlaywrightStepPassed = waitUntilOneOfTheElementsIsEnabled(args[1].toString());
                    break;
                case "clickAndReturnAlertText":
                    return clickAndReturnAlertText(args[1].toString());
                case "clickAndAcceptAlertIfPresent":
                    isPlaywrightStepPassed = clickAndAcceptAlertIfPresent(args[1].toString());
                    break;
                case "getTextFromElement":
                    return getTextFromLocator(args[1].toString());
                case "getCompleteTextFor":
                    return getCompleteTextFor(args[1].toString());
                case "getTextFromToTheOf":
                    return getTextFromToTheOf(args[1].toString(), args[2].toString(), args[3].toString());
                case "executeJavascript":
                    isPlaywrightStepPassed = executeJavascript(args[1].toString());
                    break;
                case "executeJavascriptOnElement":
                    isPlaywrightStepPassed = executeJavascriptOnElement(args[1].toString(), args[2].toString());
                    break;
                default:
                    throw new Exception(args[0].toString() + " method not implemented yet.");
            }
        } catch (Exception ex) {
            isPlaywrightStepPassed = false;
            UIStepReporter UIStepReporter = new UIStepReporter(++stepCounter, args, ex);
            UIStepReporter.reportStepResultWithScreenshotAndException(ReportUtility.REPORT_STATUS.FAIL, null);
            return false;
        }
        if (isPlaywrightStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isPlaywrightStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            UIStepReporter UIStepReporter = new UIStepReporter(++stepCounter, args, null);
            UIStepReporter.reportStepResultWithScreenshotAndException(status, null);
        }
        return isPlaywrightStepPassed;
    }

    private ArrayNode getAccessibilityViolations(Object[] args) throws IOException {
        ArrayNode violations = checkAccessibilityForPage();
        if (violations.size() == 0) {
            ReportUtility.reportPass(args[1].toString() + " page accessibility check passed.");
        } else {
            final String[] failureMessage = {""};
            violations.forEach(e -> failureMessage[0] += e.toString());
            List<String> violationsCategory = new ArrayList<>();
            violations.forEach(e -> violationsCategory.add(e.get("id").textValue()));
            ReportUtility.reportFail("\"" + args[1].toString() + "\" page accessibility check failed with " + violations.size() + " violations: " + violationsCategory);
            ReportUtility.reportJsonAsInfo("Violations:", failureMessage[0]);
        }
        return violations;
    }

    private boolean forRequestUseMockStatusAndResponse(String requestPattern, int mockHttpStatus, String
            mockResponse) {
        playwrightPage.route(requestPattern, route -> {
            Map<String, String> headers = new HashMap<>(route.request().headers());
            headers.put("Access-Control-Allow-Origin", "*");
            route.fulfill(new Route.FulfillOptions()
                    .setHeaders(headers)
                    .setStatus(mockHttpStatus)
                    .setBody(mockResponse));
        });
        return true;
    }

    private void savePlaywrightVideoIfEnabled() {
        if (SetUp.isVideoRecordingEnabled()) {
            String browserVersion = (SetUp.browserVersion != null && SetUp.browserVersion.trim().length() > 0) ? "_" + SetUp.browserVersion : "";
            String parentFolder = String.valueOf(playwrightPage.video().path().getParent());
            String extension = FilenameUtils.getExtension(playwrightPage.video().path().getFileName().toString());
            String testName = getCurrentTestName().replace(" ", "");
            String videoName = testName + "_" + SetUp.browser + browserVersion + "." + extension;
            playwrightPage.video().saveAs(Paths.get(parentFolder + File.separator + videoName));
            playwrightPage.video().delete();
        }
    }

    //TODO: Write methods to get data from table cell.

}
