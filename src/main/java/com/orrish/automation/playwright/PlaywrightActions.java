package com.orrish.automation.playwright;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.microsoft.playwright.*;
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

    protected boolean closeCurrentTab() {
        List<Page> pages = playwrightPage.context().pages();
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i) == playwrightPage) {
                playwrightPage.close();
                playwrightPage = (i == 0) ? pages.get(i + 1) : pages.get(i - 1);
                return true;
            }
        }
        return false;
    }

    protected boolean switchToNewTab() throws Exception {
        //TODO : Find a proper fix. Wait is needed so that the tab is loaded correctly. New page open and close recognizes all tabs.
        waitSeconds(5);
        playwrightPage.context().newPage();
        List<Page> pages = playwrightPage.context().pages();
        pages.get(pages.size() - 1).close();

        if (pages.size() == 1)
            throw new Exception("There is only one page open.");

        for (Page page : pages) {
            page.waitForLoadState();
            if (page != playwrightPage) {
                playwrightPage = page;
                return true;
            }
        }
        throw new Exception("Found " + pages.size() + " pages. But the url and title for all pages are same.");
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

    protected String getFullTextFor(String text) {
        String locatorText = isPlaywrightLocator(text) ? text : "text=" + text;
        return playwrightPage.textContent(locatorText);
    }

    protected boolean executeJavascript(String jsCode) {
        playwrightPage.evaluate(jsCode);
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
                case "saveAsPdfWithName":
                    return saveAsPdfWithName(args[1].toString());
                case "uploadFile":
                    isPlaywrightStepPassed = uploadFile(args[1].toString());
                    break;
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
                case "scrollTo":
                    isPlaywrightStepPassed = scrollTo(args[1].toString());
                    break;
                case "click":
                    isPlaywrightStepPassed = click(args[1].toString(), false);
                    break;
                case "clickExactly":
                    isPlaywrightStepPassed = click(args[1].toString(), true);
                    break;
                case "rightClick":
                    isPlaywrightStepPassed = rightClick(args[1].toString());
                    break;
                case "clearText":
                    isPlaywrightStepPassed = clearText();
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
                case "clickWhicheverIsDisplayedIn":
                    isPlaywrightStepPassed = clickWhicheverIsDisplayedIn(args[1].toString());
                    break;
                case "switchToNewTab":
                    isPlaywrightStepPassed = switchToNewTab();
                    break;
                case "closeCurrentTab":
                    isPlaywrightStepPassed = closeCurrentTab();
                    break;
                case "pressKey":
                    isPlaywrightStepPassed = pressKey(args[1].toString());
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
                    isPlaywrightStepPassed = selectUnselectcheckboxeswithtext(args[1].toString(), true);
                    break;
                case "unselectCheckboxForText":
                    isPlaywrightStepPassed = selectUnselectcheckboxeswithtext(args[1].toString(), false);
                    break;
                case "selectRadioForText":
                    isPlaywrightStepPassed = selectRadioForText(args[1].toString());
                    break;
                case "selectFromDropdown":
                    isPlaywrightStepPassed = selectFromDropdown(args[1].toString(), args[2].toString());
                    break;
                case "getColumnWhere":
                    return getColumnWhere(args[1].toString(), args[2].toString());
                case "clickColumnWhere":
                    isPlaywrightStepPassed = clickColumnWhere(args[1].toString(), args[2].toString());
                    break;
                case "clickInColumnWhere":
                    isPlaywrightStepPassed = clickInColumnWhere(args[1].toString(), args[2].toString(), args[3].toString());
                    break;
                case "typeInColumnWhere":
                    isPlaywrightStepPassed = typeInColumnWhere(args[1].toString(), args[2].toString(), args[3].toString());
                    break;
                case "isTextPresentInWebpage":
                    isPlaywrightStepPassed = isTextPresentInWebpage(args[1].toString());
                    break;
                case "waitUntilElementContains":
                    isPlaywrightStepPassed = waitUntilElementContains(args[1].toString(), args[2].toString());
                    break;
                case "waitUntilIsDisplayed":
                    isPlaywrightStepPassed = waitUntilIsDisplayed(args[1].toString());
                    break;
                case "waitUntilElementDoesNotContain":
                    isPlaywrightStepPassed = waitUntilElementDoesNotContain(args[1].toString(), args[2].toString());
                    break;
                case "waitUntilIsGone":
                    isPlaywrightStepPassed = waitUntilIsGone(args[1].toString());
                    break;
                case "waitUntilOneOfIsDisplayed":
                    isPlaywrightStepPassed = waitUntilOneOfIsDisplayed(args[1].toString());
                    break;
                case "waitUntilOneOfTheElementsIsEnabled":
                    isPlaywrightStepPassed = waitUntilOneOfTheElementsIsEnabled(args[1].toString());
                    break;
                case "clickAndReturnAlertText":
                    return clickAndReturnAlertText(args[1].toString());
                case "clickAndAcceptAlertIfPresent":
                    isPlaywrightStepPassed = clickAndAcceptAlertIfPresent(args[1].toString());
                    break;
                case "getFullTextFor":
                    return getFullTextFor(args[1].toString());
                case "getTextFromToTheOf":
                    return getTextFromToTheOf(args[1].toString(), args[2].toString(), args[3].toString());
                case "executeJavascript":
                    isPlaywrightStepPassed = executeJavascript(args[1].toString());
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

}
