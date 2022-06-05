package com.orrish.automation.playwright;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
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
        playwrightPage.navigate(url);
        return true;
    }

    protected boolean navigateTo(String url) {
        playwrightPage.navigate(url);
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

    private boolean hoverOn(String text) throws Exception {
        getFirstElementWithExactText(text).hover();
        return true;
    }

    private boolean clickElement(String text, boolean isExact) throws Exception {
        waitUntilTextIsDisplayed(text);
        Locator locator = null;
        try {
            locator = isExact ? getFirstElementWithExactText(text) : getAllElementsContainingText(text).first();
            locator.click();
        } catch (TimeoutError ex) {
            //Force click it per comment in https://github.com/microsoft/playwright/issues/12298#issuecomment-1051261068
            locator.click(new Locator.ClickOptions().setForce(true));
        }
        playwrightPage.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return true;
    }

    protected boolean clickAndClearText(String text) {
        waitUntilTextIsDisplayed(text);
        Locator locator = getAllElementsContainingText(text).first();
        locator.click();
        locator.fill("");
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

        Locator icons = getIconCorrespondingTo(textToClick, "svg");
        icons = (icons.count() == 0) ? getIconCorrespondingTo(textToClick, "img") : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(textToClick, "button") : icons;

        if (icons.count() == 0) {
            throw new Exception("Could not find icon with the given criteria.");
        }
        icons.first().click();
        return true;
    }

    protected Locator getRelativeImages(String iconTextToClick, String direction, String textToFind) throws Exception {
        //svg:right-of(:text("Home"))
        String relativeLocatorString = ":" + getDirection(direction) + "(:text(\"" + textToFind + "\"))";
        Locator icons = getIconCorrespondingTo(iconTextToClick, "svg" + relativeLocatorString);
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "img" + relativeLocatorString) : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "button" + relativeLocatorString) : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "a" + relativeLocatorString) : icons;
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
        Locator icons = getRelativeImages(iconToClick, "right", textToFind);
        icons = icons.count() == 0 ? getRelativeImages(iconToClick, "left", textToFind) : icons;
        if (icons.count() == 0) {
            throw new Exception("Could not find icon with the given criteria.");
        }
        icons.first().click();
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
            Locator locators = getAllElementsContainingText(locator);
            for (int i = 0; i < locators.count(); i++) {
                Locator eachLocator = locators.nth(i);
                String tagName = eachLocator.elementHandle().getProperty("tagName").jsonValue().toString();
                if (tagName.equalsIgnoreCase("label") || tagName.equalsIgnoreCase("input")) {
                    eachLocator.fill(value);
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean typeIn(String value, String locator) {
        boolean locatorFound = false;
        List<String> inputElements = Arrays.asList(new String[]{"input", "textarea", "contenteditable"});
        if (inputElements.contains(locator)) {
            playwrightPage.locator(locator).first().fill(value);
            return true;
        }
        for (int i = 0; i < SetUp.defaultWaitTime && !locatorFound; i++) {
            playwrightPage.waitForSelector("text=" + locator + " >> visible=true");
            Locator locators = playwrightPage.locator("text=" + locator + " >> visible=true");
            if (locators.count() > 0) {
                locators.first().fill(value);
                return true;
            }
            locators = playwrightPage.locator("xpath=//input[contains(@placeholder,'" + locator + "')]");
            if (locators.count() > 0) {
                locators.first().fill(value);
                return true;
            }
            waitSeconds(1);
        }
        Locator locators = getAllElementsContainingText(locator);
        for (int i = 0; i < locators.count(); i++) {
            Locator eachLocator = locators.nth(i);
            String tagName = eachLocator.elementHandle().getProperty("tagName").jsonValue().toString();
            if (tagName.equalsIgnoreCase("label") || tagName.equalsIgnoreCase("input")) {
                eachLocator.fill(value);
                return true;
            }
        }
        return false;
    }

    protected boolean typeInTextFieldNumber(String text, int whichField) {
        playwrightPage.waitForSelector("input");
        Locator locator = playwrightPage.locator("input");
        locator.nth(whichField - 1).fill(text); //Convert to zero based index.
        return true;
    }

    protected boolean selectRadioForText(String text) throws Exception {
        Locator radioHandles = playwrightPage.locator("[type=radio]:left-of(:text(\"" + text + "\"))  >> visible=true");
        if (radioHandles.count() == 0) {
            radioHandles = playwrightPage.locator("xpath=//label[contains(.,'" + text + "')]  >> visible=true");
        }
        if (radioHandles.count() == 0)
            throw new Exception("Could not find a radio button with text " + text);
        try {
            radioHandles.first().click();
        } catch (TimeoutError ex) {
            radioHandles.first().click(new Locator.ClickOptions().setForce(true));
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
        Locator allElements = playwrightPage.locator("text=" + text + " >> visible=true");
        for (int i = 0; i < allElements.count(); i++) {
            if (allElements.nth(i).textContent().contains(text))
                return true;
        }
        return false;
    }

    protected String clickAndReturnAlertText(String locatorText) throws Exception {
        final String[] message = new String[1];
        playwrightPage.onDialog(dialog -> {
            message[0] = dialog.message();
        });
        Locator locator = getFirstElementWithExactText(locatorText);
        locator.click();
        return message[0];
    }

    protected boolean clickAndAcceptAlertIfPresent(String locatorText) {
        playwrightPage.onDialog(dialog -> {
            ReportUtility.reportInfo("Alert with text \"" + dialog.message() + "\" on clicking " + locatorText + " is accepted.");
            dialog.accept();
        });
        Locator locator = getAllElementsContainingText(locatorText);
        locator.first().click();
        return true;
    }

    protected boolean waitUntilElementIsGone(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            Locator locator = playwrightPage.locator(locatorText);
            if (locator == null || !locator.isVisible())
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilTextIsGone(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            Locator locator = playwrightPage.locator("text=" + locatorText + " >> visible=true");
            if (locator.count() == 0)
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
                case "hoverOn":
                    isPlaywrightStepPassed = hoverOn(args[1].toString());
                    break;
                case "click":
                    isPlaywrightStepPassed = clickElement(args[1].toString(), false);
                    break;
                case "clickExactly":
                    isPlaywrightStepPassed = clickElement(args[1].toString(), true);
                    break;
                case "clickAndClearText":
                    isPlaywrightStepPassed = clickAndClearText(args[1].toString());
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
