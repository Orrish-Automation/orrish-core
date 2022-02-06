package com.orrish.automation.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.report.UIStepReporter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentTestName;
import static com.orrish.automation.entrypoint.SetUp.*;

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
        playwrightPage.navigate(url);
        return true;
    }

    protected boolean maximizeTheWindow() throws Exception {
        throw new Exception("Not implemented. Track issue at https://github.com/microsoft/playwright/issues/4046");
    }

    protected boolean takeWebScreenshotWithText(String text) {
        ReportUtility.reportWithScreenshot(null, text, ReportUtility.REPORT_STATUS.INFO, text);
        return true;
    }

    protected boolean clickFor(String locator) {
        playwrightPage.click(locator);
        playwrightPage.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return true;
    }

    protected boolean clickWithText(String locator, String text) {
        playwrightPage.locator(locator + ":has-text(\"" + text + "\")").click();
        return true;
    }

    protected boolean clickRowContainingText(String text) {
        playwrightPage.locator("tr:has-text(\"" + text + "\")").click();
        return true;
    }

    protected boolean clickWhicheverIsDisplayedIn(String locator) {
        playwrightPage.locator(locator).click();
        return true;
    }

    protected boolean enterInTextFieldFor(String value, String locator) {
        playwrightPage.fill(locator, value);
        return true;
    }

    protected boolean enterInTextFieldNumber(String text, int whichField) {
        playwrightPage.waitForSelector("input");
        List<ElementHandle> elementHandleList = playwrightPage.querySelectorAll("input");
        elementHandleList.get(whichField - 1).fill(text); //Convert to zero based index.
        return true;
    }

    protected boolean isTextPresentInWebpage(String text) {
        ElementHandle value = playwrightPage.querySelector("text=" + text);
        return value.textContent().contains(text);
    }

    protected String clickAndReturnAlertText(String locator) {
        final String[] message = new String[1];
        playwrightPage.onDialog(dialog -> {
            message[0] = dialog.message();
            dialog.dismiss();
        });
        playwrightPage.click(locator);
        return message[0];
    }

    protected boolean dismissAlertIfPresent() {
        ReportUtility.reportInfo("Playwright by default dismisses alerts. So, no action taken.");
        return true;
    }

    protected boolean clickAndAcceptAlertIfPresent(String locator) {
        playwrightPage.onDialog(dialog -> {
            ReportUtility.reportInfo("Alert with text \"" + dialog.message() + "\" on clicking " + locator + " is accepted.");
            dialog.accept();
        });
        playwrightPage.click(locator);
        return true;
    }

    protected boolean selectFromDropdown(String value, String locator) {
        playwrightPage.selectOption(locator, new SelectOption().setLabel(value));
        return true;
    }

    protected boolean selectUnselectCheckboxesWithText(String value, boolean shouldBeSelected) {
        if (shouldBeSelected)
            playwrightPage.check("text=" + value);
        else
            playwrightPage.uncheck("text=" + value);
        return true;
    }

    protected boolean waitUntilIsGoneFor(String locator) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            ElementHandle elementHandle = playwrightPage.querySelector(locator);
            if (elementHandle == null)
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilIsDisplayedFor(String locator) {
        return playwrightPage.waitForSelector(locator).isVisible();
    }

    protected boolean waitUntilOneOfTheLocatorsIsDisplayed(String locator) {
        return waitUntilOneOfTheLocators(locator, "visible");
    }

    protected boolean waitUntilOneOfTheElementIsEnabled(String locator) {
        return waitUntilOneOfTheLocators(locator, "enabled");
    }

    private boolean waitUntilOneOfTheLocators(String locator, String value) {
        String[] locators = locator.split(",,");
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            for (String eachLocator : locators) {
                try {
                    if (playwrightPage.isVisible(eachLocator)) {
                        if ("visible".contains(value)) {
                            return true;
                        } else if ("enabled".contains(value)) {
                            if (playwrightPage.isEnabled(eachLocator))
                                return true;
                        }
                    }
                } catch (Exception ex) {
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
        return playwrightPage.textContent(locator);
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
                case "takeWebScreenshotWithText":
                    return takeWebScreenshotWithText(args[1].toString());
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
                case "waitUntilOneOfTheLocatorsIsDisplayed":
                    isPlaywrightStepPassed = waitUntilOneOfTheLocatorsIsDisplayed(args[1].toString());
                    break;
                case "waitUntilOneOfTheLocatorsIsEnabled":
                    isPlaywrightStepPassed = waitUntilOneOfTheElementIsEnabled(args[1].toString());
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
                case "getTextFromLocator":
                    return getTextFromLocator(args[1].toString());
                case "executeJavascript":
                    isPlaywrightStepPassed = executeJavascript(args[1].toString());
                    break;
                default:
                    throw new Exception(args[0].toString() + " method not implemented yet.");
            }
        } catch (Exception ex) {
            isPlaywrightStepPassed = false;
            UIStepReporter UIStepReporter = new UIStepReporter(++SetUp.stepCounter, args, ex);
            UIStepReporter.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.FAIL, null);
            return false;
        }
        if (isPlaywrightStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isPlaywrightStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            UIStepReporter UIStepReporter = new UIStepReporter(++SetUp.stepCounter, args, null);
            UIStepReporter.reportStepResultWithScreenshot(status, null);
        }
        return isPlaywrightStepPassed;
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
