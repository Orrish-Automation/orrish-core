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
import java.lang.reflect.Method;
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

    public boolean launchBrowserAndNavigateTo(String url) {

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

    public boolean inBrowserNavigateTo(String url) {
        playwrightPage.navigate(url);
        return true;
    }

    public boolean maximizeTheWindow() throws Exception {
        throw new Exception("Not implemented. Track issue at https://github.com/microsoft/playwright/issues/4046");
    }

    public boolean inBrowserNavigateBack() {
        playwrightPage.goBack();
        return true;
    }

    public boolean refreshWebPage() {
        playwrightPage.reload();
        return true;
    }

    public String getPageUrl() {
        return playwrightPage.url();
    }

    public String getPageTitle() {
        return playwrightPage.title();
    }

    public boolean takeWebScreenshotWithText(String text) {
        ReportUtility.reportWithScreenshot(null, text, ReportUtility.REPORT_STATUS.INFO, text);
        return true;
    }

    public boolean saveAsPdfWithName(String name) {
        playwrightPage.pdf(new Page.PdfOptions().setPath(Paths.get(name)));
        return true;
    }

    public boolean switchToNewTab() throws Exception {
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

    public boolean closeCurrentTab() {
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

    public boolean checkAccessibilityForPage(String pageTitle) throws IOException {
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

    public ArrayNode getAccessibilityViolationsForCurrentPage() throws IOException {
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


    public boolean executeJavascript(String jsCode) {
        playwrightPage.evaluate(jsCode);
        return true;
    }

    public String executeOnWebAndReturnString(Object... args) {
        Object valueToReturn = executeOnWebAndReturnObject(args);
        if (valueToReturn == null)
            return "";
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " returned : " + valueToReturn);
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
            int numberOfParametersPassed = args.length - 1;
            Method[] methods = this.getClass().getMethods();
            boolean methodFound = false;
            for (Method method : methods) {
                if (method.getName().equals(args[0])) {
                    methodFound = true;
                    Object valueToReturn = null;
                    if (numberOfParametersPassed == 0)
                        valueToReturn = getMethod(method).invoke(this);
                    else if (numberOfParametersPassed == 1)
                        valueToReturn = getMethod(method).invoke(this, args[1]);
                    else if (numberOfParametersPassed == 2)
                        valueToReturn = getMethod(method).invoke(this, args[1], args[2]);
                    else if (numberOfParametersPassed == 3)
                        valueToReturn = getMethod(method).invoke(this, args[1], args[2], args[3]);

                    String methodReturnType = method.getReturnType().getSimpleName();
                    if (!methodReturnType.equals("boolean")) {
                        return valueToReturn;
                    }
                    //Don't fail the test for accessibility because we may want to continue the test to go on even if accessibility check fails.
                    isPlaywrightStepPassed = method.getName().equals("checkAccessibilityForPage") ? isPlaywrightStepPassed : (boolean) valueToReturn;
                    break;
                }
            }
            if (!methodFound) {
                throw new Exception(args[0] + " not implemented yet.");
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

    private Method getMethod(Method method) throws NoSuchMethodException {
        int count = method.getParameters().length;
        if (count == 1)
            return this.getClass().getMethod(method.getName(), String.class);
        if (count == 2)
            return this.getClass().getMethod(method.getName(), String.class, String.class);
        if (count == 3)
            return this.getClass().getMethod(method.getName(), String.class, String.class, String.class);
        if (count == 4)
            return this.getClass().getMethod(method.getName(), String.class, String.class, String.class, String.class);
        if (count == 5)
            return this.getClass().getMethod(method.getName(), String.class, String.class, String.class, String.class, String.class);
        return method;
    }

    public boolean forRequestUseMockStatusAndResponse(String requestPattern, String mockHttpStatus, String mockResponse) {
        return forRequestUseMockStatusAndResponse(requestPattern, Integer.parseInt(mockHttpStatus), mockResponse);
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
