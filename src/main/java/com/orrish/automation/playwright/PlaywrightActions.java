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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;
import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;

public class PlaywrightActions {

    protected static Playwright playwright;
    protected static Page playwrightPage;
    protected boolean isPlaywrightStepPassed = true;

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

    public void quitPlaywright() {
        if (!conditionalStep) return;
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
    }

    protected boolean launchBrowserAndNavigateTo(String url) {
        if (!conditionalStep) return true;

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
        playwrightPage.setDefaultTimeout(defaultWaitTime * 1000);
        playwrightPage.navigate(url);
        return true;
    }

    protected boolean navigateTo(String url) {
        if (!conditionalStep) return true;
        playwrightPage.navigate(url);
        return true;
    }

    protected boolean maximizeTheWindow() throws Exception {
        if (!conditionalStep) return true;
        throw new Exception("Not implemented. Track issue at https://github.com/microsoft/playwright/issues/4046");
    }

    protected boolean inBrowserNavigateBack() {
        if (!conditionalStep) return true;
        playwrightPage.goBack();
        return true;
    }

    protected boolean refreshWebPage() {
        if (!conditionalStep) return true;
        playwrightPage.reload();
        return true;
    }

    protected boolean takeWebScreenshotWithText(String text) {
        if (!conditionalStep) return true;
        ReportUtility.reportWithScreenshot(null, text, ReportUtility.REPORT_STATUS.INFO, text);
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

    protected boolean clickFor(String locator) {
        if (!conditionalStep) return true;
        playwrightPage.click(locator);
        playwrightPage.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return true;
    }

    protected boolean clickWithText(String locator, String text) {
        if (!conditionalStep) return true;
        playwrightPage.locator(locator + ":has-text(\"" + text + "\")").click();
        return true;
    }

    protected boolean clickRowContainingText(String text) {
        if (!conditionalStep) return true;
        playwrightPage.locator("tr:has-text(\"" + text + "\")").first().click();
        return true;
    }

    protected boolean clickWhicheverIsDisplayedIn(String locator) {
        if (!conditionalStep) return true;
        String[] eachParts = locator.split(",,");
        for (String eachPart : eachParts) {
            Locator probableLocator = playwrightPage.locator(eachPart);
            if (probableLocator.count() > 0) {
                probableLocator.click();
                return true;
            }
        }
        return false;
    }

    protected boolean enterInTextFieldFor(String value, String locator) {
        if (!conditionalStep) return true;
        playwrightPage.fill(locator, value);
        return true;
    }

    protected boolean enterInTextFieldNumber(String text, int whichField) {
        if (!conditionalStep) return true;
        playwrightPage.waitForSelector("input");
        List<ElementHandle> elementHandleList = playwrightPage.querySelectorAll("input");
        elementHandleList.get(whichField - 1).fill(text); //Convert to zero based index.
        return true;
    }

    protected boolean isTextPresentInWebpage(String text) {
        if (!conditionalStep) return true;
        ElementHandle value = playwrightPage.querySelector("text=" + text);
        return value.textContent().contains(text);
    }

    protected String clickAndReturnAlertText(String locator) {
        if (!conditionalStep) return "";
        final String[] message = new String[1];
        playwrightPage.onDialog(dialog -> {
            message[0] = dialog.message();
            dialog.dismiss();
        });
        playwrightPage.click(locator);
        return message[0];
    }

    protected boolean dismissAlertIfPresent() {
        if (!conditionalStep) return true;
        ReportUtility.reportInfo("Playwright by default dismisses alerts. So, no action taken.");
        return true;
    }

    protected boolean clickAndAcceptAlertIfPresent(String locator) {
        if (!conditionalStep) return true;
        playwrightPage.onDialog(dialog -> {
            ReportUtility.reportInfo("Alert with text \"" + dialog.message() + "\" on clicking " + locator + " is accepted.");
            dialog.accept();
        });
        playwrightPage.click(locator);
        return true;
    }

    protected boolean selectFromDropdown(String value, String locator) {
        if (!conditionalStep) return true;
        playwrightPage.selectOption(locator, new SelectOption().setLabel(value));
        return true;
    }

    protected boolean selectUnselectCheckboxesWithText(String value, boolean shouldBeSelected) {
        if (!conditionalStep) return true;
        if (shouldBeSelected)
            playwrightPage.check("input:has-text(\"" + value + "\")");
        else
            playwrightPage.uncheck("input:has-text(\"" + value + "\")");
        return true;
    }

    protected boolean waitUntilIsGoneFor(String locator) {
        if (!conditionalStep) return true;
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            ElementHandle elementHandle = playwrightPage.querySelector(locator);
            if (elementHandle == null)
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilIsDisplayedFor(String locator) {
        if (!conditionalStep) return true;
        return playwrightPage.waitForSelector(locator).isVisible();
    }

    protected boolean waitUntilOneOfTheElementsIsDisplayed(String locator) {
        return waitUntilOneOfTheElements(locator, "visible");
    }

    protected boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        return waitUntilOneOfTheElements(locator, "enabled");
    }

    private boolean waitUntilOneOfTheElements(String locator, String value) {
        if (!conditionalStep) return true;
        String[] locators = locator.split(",,");
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            for (String eachLocator : locators) {
                if (playwrightPage.locator(eachLocator).count() > 0) {
                    if (playwrightPage.isVisible(eachLocator)) {
                        if ("visible".contains(value)) {
                            return true;
                        } else if ("enabled".contains(value)) {
                            if (playwrightPage.isEnabled(eachLocator))
                                return true;
                        }
                    }
                }
            }
            waitSeconds(1);
        }
        return false;
    }

    private boolean waitUntilElementTextContains(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, true);
    }

    private boolean waitUntilElementTextDoesNotContain(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, false);
    }

    private boolean waitUntilElementTextCheck(String locator, String text, boolean shouldContain) {
        if (!conditionalStep) return true;
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            if (shouldContain && playwrightPage.textContent(locator).contains(text))
                return true;
            if (!shouldContain && !playwrightPage.textContent(locator).contains(text))
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected String getTextFromLocator(String locator) {
        if (!conditionalStep) return "";
        return playwrightPage.textContent(locator);
    }

    protected boolean executeJavascript(String jsCode) {
        if (!conditionalStep) return true;
        playwrightPage.evaluate(jsCode);
        return true;
    }

    protected boolean executeJavascriptOnElement(String jsCode, String locator) {
        if (!conditionalStep) return true;
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
                case "takeWebScreenshotWithText":
                    return takeWebScreenshotWithText(args[1].toString());
                case "getPageTitle":
                    return playwrightPage.title();
                case "forRequestUseMockStatusAndResponse":
                    return forRequestUseMockStatusAndResponse(args[1].toString(), Integer.parseInt(args[2].toString()), args[3].toString());
                case "checkAccessibilityForPage":
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
                    return violations.size() == 0;
                case "clickFor":
                    isPlaywrightStepPassed = clickFor(args[1].toString());
                    break;
                case "clickWithText":
                    isPlaywrightStepPassed = clickWithText(args[1].toString(), args[2].toString());
                    break;
                case "clickWhicheverIsDisplayedIn":
                    isPlaywrightStepPassed = clickWhicheverIsDisplayedIn(args[1].toString());
                    break;
                case "clickRowContainingText":
                    return clickRowContainingText(args[1].toString());
                case "selectCheckboxForText":
                    isPlaywrightStepPassed = selectUnselectCheckboxesWithText(args[1].toString(), true);
                    break;
                case "unselectCheckboxForText":
                    isPlaywrightStepPassed = selectUnselectCheckboxesWithText(args[1].toString(), false);
                    break;
                case "waitUntilElementTextContains":
                    isPlaywrightStepPassed = waitUntilElementTextContains(args[1].toString(), args[2].toString());
                    break;
                case "waitUntilElementTextDoesNotContain":
                    isPlaywrightStepPassed = waitUntilElementTextDoesNotContain(args[1].toString(), args[2].toString());
                    break;
                case "waitUntilIsGoneFor":
                    isPlaywrightStepPassed = waitUntilIsGoneFor(args[1].toString());
                    break;
                case "waitUntilIsDisplayedFor":
                    isPlaywrightStepPassed = waitUntilIsDisplayedFor(args[1].toString());
                    break;
                case "waitUntilOneOfTheElementsIsDisplayed":
                    isPlaywrightStepPassed = waitUntilOneOfTheElementsIsDisplayed(args[1].toString());
                    break;
                case "waitUntilOneOfTheElementsIsEnabled":
                    isPlaywrightStepPassed = waitUntilOneOfTheElementsIsEnabled(args[1].toString());
                    break;
                case "enterInTextFieldFor":
                    isPlaywrightStepPassed = enterInTextFieldFor(args[1].toString(), args[2].toString());
                    break;
                case "enterInTextFieldNumber":
                    isPlaywrightStepPassed = enterInTextFieldNumber(args[1].toString(), Integer.parseInt(args[2].toString()));
                    break;
                case "isTextPresentInWebpage":
                    isPlaywrightStepPassed = isTextPresentInWebpage(args[1].toString());
                    break;
                case "clickAndReturnAlertText":
                    return clickAndReturnAlertText(args[1].toString());
                case "dismissAlertIfPresent":
                    isPlaywrightStepPassed = dismissAlertIfPresent();
                    break;
                case "clickAndAcceptAlertIfPresent":
                    isPlaywrightStepPassed = clickAndAcceptAlertIfPresent(args[1].toString());
                    break;
                case "selectFromDropdown":
                    isPlaywrightStepPassed = selectFromDropdown(args[1].toString(), args[2].toString());
                    break;
                case "getTextFromElement":
                    return getTextFromLocator(args[1].toString());
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
            UIStepReporter UIStepReporter = new UIStepReporter(++SetUp.stepCounter, args, ex);
            UIStepReporter.reportStepResultWithScreenshotAndException(ReportUtility.REPORT_STATUS.FAIL, null);
            return false;
        }
        if (isPlaywrightStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isPlaywrightStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            UIStepReporter UIStepReporter = new UIStepReporter(++SetUp.stepCounter, args, null);
            UIStepReporter.reportStepResultWithScreenshotAndException(status, null);
        }
        return isPlaywrightStepPassed;
    }

    private boolean forRequestUseMockStatusAndResponse(String requestPattern, int mockHttpStatus, String mockResponse) {
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

}
