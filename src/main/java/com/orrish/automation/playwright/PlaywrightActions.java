package com.orrish.automation.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.orrish.automation.entrypoint.ReportSteps;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.model.TestStepReportModel;
import com.orrish.automation.utility.report.ReportUtility;

import java.util.List;

import static com.orrish.automation.entrypoint.GeneralSteps.getMethodStyleStepName;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.SetUp.isScreenshotAtEachStepEnabled;

public class PlaywrightActions {

    protected boolean isPlaywrightStepPassed = true;

    protected boolean launchBrowserAndNavigateTo(String url) {

        String testName = ReportSteps.getCurrentTestName();
        SetUp.playwright = Playwright.create();
        Browser browser;
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(SetUp.isPlaywrightHeadless);

        switch (SetUp.browser.trim().toUpperCase()) {
            case "CHROME":
                browser = SetUp.playwright.chromium().launch(launchOptions);
                break;
            case "FIREFOX":
                browser = SetUp.playwright.firefox().launch(launchOptions);
                break;
            case "SAFARI":
                browser = SetUp.playwright.webkit().launch(launchOptions);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + SetUp.browser.trim().toUpperCase());
        }
        Browser.NewContextOptions browserContext = new Browser.NewContextOptions().setIgnoreHTTPSErrors(true);
        BrowserContext context = browser.newContext(browserContext);

        SetUp.playwrightPage = context.newPage();
        SetUp.playwrightPage.navigate(url);
        String videoName = testName + "_" + SetUp.browser + "_" + browser.version();
        return true;
    }

    protected boolean navigateTo(String url) {
        SetUp.playwrightPage.navigate(url);
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
        SetUp.playwrightPage.click(locator);
        SetUp.playwrightPage.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return true;
    }

    protected boolean clickWithText(String locator, String text) {
        SetUp.playwrightPage.locator(locator + ":has-text(\"" + text + "\")").click();
        return true;
    }

    protected boolean clickRowContainingText(String text) {
        SetUp.playwrightPage.locator("tr:has-text(\"" + text + "\")").click();
        return true;
    }

    protected boolean clickWhicheverIsDisplayedIn(String locator) {
        SetUp.playwrightPage.locator(locator).click();
        return true;
    }

    protected boolean enterInTextFieldFor(String value, String locator) {
        SetUp.playwrightPage.fill(locator, value);
        return true;
    }

    protected boolean enterInTextFieldNumber(String text, int whichField) {
        SetUp.playwrightPage.waitForSelector("input");
        List<ElementHandle> elementHandleList = SetUp.playwrightPage.querySelectorAll("input");
        elementHandleList.get(whichField - 1).fill(text); //Convert to zero based index.
        return true;
    }

    protected boolean isTextPresentInWebpage(String text) {
        ElementHandle value = SetUp.playwrightPage.querySelector("text=" + text);
        return value.textContent().contains(text);
    }

    protected String clickAndReturnAlertText(String locator) {
        final String[] message = new String[1];
        SetUp.playwrightPage.onDialog(dialog -> {
            message[0] = dialog.message();
            dialog.dismiss();
        });
        SetUp.playwrightPage.click(locator);
        return message[0];
    }

    protected boolean dismissAlertIfPresent() {
        ReportUtility.reportInfo("Playwright by default dismisses alerts. So, no action taken.");
        return true;
    }

    protected boolean clickAndAcceptAlertIfPresent(String locator) {
        SetUp.playwrightPage.onDialog(dialog -> {
            ReportUtility.reportInfo("Alert with text \"" + dialog.message() + "\" on clicking " + locator + " is accepted.");
            dialog.accept();
        });
        SetUp.playwrightPage.click(locator);
        return true;
    }

    protected boolean selectFromDropdown(String value, String locator) {
        SetUp.playwrightPage.selectOption(locator, new SelectOption().setLabel(value));
        return true;
    }

    protected boolean selectUnselectCheckboxesWithText(String value, boolean shouldBeSelected) {
        if (shouldBeSelected)
            SetUp.playwrightPage.check("text=" + value);
        else
            SetUp.playwrightPage.uncheck("text=" + value);
        return true;
    }

    protected boolean waitUntilIsGoneFor(String locator) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            ElementHandle elementHandle = SetUp.playwrightPage.querySelector(locator);
            if (elementHandle == null)
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilIsDisplayedFor(String locator) {
        return SetUp.playwrightPage.waitForSelector(locator).isVisible();
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
                    if (SetUp.playwrightPage.isVisible(eachLocator)) {
                        if ("visible".contains(value)) {
                            return true;
                        } else if ("enabled".contains(value)) {
                            if (SetUp.playwrightPage.isEnabled(eachLocator))
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
            if (shouldContain && SetUp.playwrightPage.textContent(locator).contains(text))
                return true;
            if (!shouldContain && !SetUp.playwrightPage.textContent(locator).contains(text))
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected String getTextFromLocator(String locator) {
        return SetUp.playwrightPage.textContent(locator);
    }

    protected boolean executeJavascript(String jsCode) {
        SetUp.playwrightPage.evaluate(jsCode);
        return true;
    }

    public String executeOnWebAndReturnString(Object... args) {
        if (!isPlaywrightStepPassed) {
            ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
            return "";
        }
        String value = executeOnWebAndReturnObject(args).toString();
        if (isScreenshotAtEachStepEnabled) {
            TestStepReportModel testStepReportModel = new TestStepReportModel(++SetUp.stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.INFO, null);
        }
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " returned " + value);
        return value;
    }

    public boolean executeOnWebAndReturnBoolean(Object... args) {
        if (!(args.length > 0) || isPlaywrightStepPassed) {
        } else if (!isPlaywrightStepPassed) {
            ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
            return false;
        }
        return Boolean.parseBoolean(executeOnWebAndReturnObject(args).toString());
    }

    protected Object executeOnWebAndReturnObject(Object... args) {
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
            TestStepReportModel testStepReportModel = new TestStepReportModel(++SetUp.stepCounter, args, ex);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.FAIL, null);
            return false;
        }
        if (isPlaywrightStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isPlaywrightStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            TestStepReportModel testStepReportModel = new TestStepReportModel(++SetUp.stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(status, null);
        }
        return isPlaywrightStepPassed;
    }

}
