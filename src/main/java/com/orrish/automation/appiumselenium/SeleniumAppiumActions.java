package com.orrish.automation.appiumselenium;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.model.TestStepReportModel;
import com.orrish.automation.utility.report.ReportUtility;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.orrish.automation.entrypoint.GeneralSteps.getMethodStyleStepName;
import static com.orrish.automation.entrypoint.SetUp.*;

public class SeleniumAppiumActions {

    protected boolean isWebStepPassed = true;
    protected boolean isMobileStepPassed = true;
    protected PageMethods pageMethods = PageMethods.getInstance();

    public String executeOnWebAndReturnString(Object... args) {
        if (!isWebStepPassed) {
            ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
            return "";
        }
        String value = executeOnWebAndReturnObject(args).toString();
        if (isScreenshotAtEachStepEnabled) {
            TestStepReportModel testStepReportModel = new TestStepReportModel(++stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.INFO, webDriver);
        }
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " returned " + value);
        return value;
    }

    public String executeOnMobileAndReturnString(Object... args) {
        if (!isMobileStepPassed) {
            ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
            return "";
        }
        String value = executeOnMobileAndReturnObject(args).toString();
        if (isScreenshotAtEachStepEnabled) {
            TestStepReportModel testStepReportModel = new TestStepReportModel(++stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.INFO, appiumDriver);
        }
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " returned " + value);
        return value;
    }

    public boolean executeOnWebAndReturnBoolean(Object... args) {
        if (isWebStepPassed) {
            return Boolean.parseBoolean(executeOnWebAndReturnObject(args).toString());
        }
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
        return false;
    }

    public boolean executeOnMobileAndReturnBoolean(Object... args) {
        if (isMobileStepPassed) {
            return Boolean.parseBoolean(executeOnMobileAndReturnObject(args).toString());
        }
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
        return false;
    }

    protected Object executeOnMobileAndReturnObject(Object... args) {
        try {
            switch (args[0].toString()) {
                case "launchAppOnDevice":
                    isMobileStepPassed = pageMethods.launchAppOnDevice();
                    break;
                case "takeMobileScreenshotWithText":
                    isMobileStepPassed = pageMethods.takeMobileScreenshotWithText(args[1].toString());
                    break;
                case "closeAppOnDevice":
                    isMobileStepPassed = pageMethods.closeAppOnDevice();
                    break;
                case "inMobileGoBackToPreviousPage":
                    isMobileStepPassed = pageMethods.navigateBack(appiumDriver);
                    break;
                case "pressHomeKey":
                    isMobileStepPassed = pageMethods.pressHomeKey();
                    break;
                case "pressBackKey":
                    isMobileStepPassed = pageMethods.pressBackKey();
                    break;
                case "swipeOnceVertically":
                    isMobileStepPassed = pageMethods.swipeOnceVertically();
                    break;
                case "tapFor":
                    isMobileStepPassed = pageMethods.clickFor(appiumDriver, args[1].toString());
                    break;
                case "tapWithText":
                    isMobileStepPassed = pageMethods.clickWithText(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "tapWhicheverIsDisplayedIn":
                    isMobileStepPassed = pageMethods.clickWhicheverIsDisplayedIn(appiumDriver, args[1].toString());
                    break;
                case "inMobileWaitUntilIsGoneFor":
                    isMobileStepPassed = pageMethods.waitUntilIsGoneFor(appiumDriver, args[1].toString());
                    break;
                case "inMobileWaitUntilIsDisplayedFor":
                    isMobileStepPassed = pageMethods.waitUntilIsDisplayedFor(appiumDriver, args[1].toString());
                    break;
                case "inMobileWaitUntilOneOfTheLocatorsIsEnabled":
                    isMobileStepPassed = (pageMethods.waitUntilOneOfTheLocatorsIsEnabled(appiumDriver, args[1].toString()) != null);
                    break;
                case "inMobileWaitUntilElementTextContains":
                    isMobileStepPassed = pageMethods.waitUntilElementTextContains(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "inMobileWaitUntilElementTextDoesNotContain":
                    isMobileStepPassed = pageMethods.waitUntilElementTextDoesNotContain(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "inMobileEnterInTextFieldFor":
                    isMobileStepPassed = pageMethods.enterInTextFieldFor(appiumDriver, args[1].toString(), args[2].toString());
                    break;
                case "inMobileGetTextFromLocator":
                    return pageMethods.getTextFromLocator(appiumDriver, args[1].toString());
                default:
                    throw new Exception(args[0].toString() + " method not implemented yet.");
            }
        } catch (Exception ex) {
            isMobileStepPassed = false;
            return reportException(appiumDriver, args, ex);
        }
        reportExecutionStatus(isMobileStepPassed, args, appiumDriver);
        return isMobileStepPassed;
    }

    protected Object executeOnWebAndReturnObject(Object... args) {
        try {
            /*
            if (args.length == 1) {
                Method method = pageMethods.getClass().getMethod(args[0].toString());
                if (!method.getReturnType().getTypeName().contains("boolean")) {
                    return method.invoke(pageMethods);
                } else {
                    isWebStepPassed = (boolean) method.invoke(pageMethods);
                }
            } else if (args.length == 2) {
                Method method = pageMethods.getClass().getMethod(args[0].toString(), String.class);
                if (!method.getReturnType().getTypeName().contains("boolean")) {
                    return method.invoke(pageMethods);
                } else {
                    isWebStepPassed = (boolean) method.invoke(pageMethods, args[1].toString());
                }
            } else {
                isWebStepPassed = false;
                throw new Exception("Method " + GeneralSteps.getMethodStyleStepName(args) + " not implemented.");
            }
             */
            switch (args[0].toString()) {
                case "launchBrowserAndNavigateTo":
                    isWebStepPassed = pageMethods.launchBrowserAndNavigateTo(args[1].toString());
                    break;
                case "inBrowserNavigateTo":
                    isWebStepPassed = pageMethods.inBrowserNavigateTo(args[1].toString());
                    break;
                case "inBrowserNavigateBack":
                    isWebStepPassed = pageMethods.navigateBack(webDriver);
                    break;
                case "closeBrowser":
                    isWebStepPassed = pageMethods.closeBrowser();
                    break;
                case "quitBrowser":
                    isWebStepPassed = pageMethods.quitBrowser();
                    break;
                case "refreshWebPage":
                    isWebStepPassed = pageMethods.refreshWebPage();
                    break;
                case "maximizeTheWindow":
                    isWebStepPassed = pageMethods.maximizeTheWindow();
                    break;
                case "takeWebScreenshotWithText":
                    return pageMethods.takeWebScreenshotWithText(args[1].toString());
                case "clickFor":
                    isWebStepPassed = pageMethods.clickFor(webDriver, args[1].toString());
                    break;
                case "clickWithText":
                    isWebStepPassed = pageMethods.clickWithText(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "clickWhicheverIsDisplayedIn":
                    isWebStepPassed = pageMethods.clickWhicheverIsDisplayedIn(webDriver, args[1].toString());
                    break;
                case "clickRowContainingText":
                    return pageMethods.clickRowContainingText(args[1].toString());
                case "selectCheckboxForText":
                    isWebStepPassed = pageMethods.selectUnselectCheckboxesForText(args[1].toString(), true);
                    break;
                case "unselectCheckboxForText":
                    isWebStepPassed = pageMethods.selectUnselectCheckboxesForText(args[1].toString(), false);
                    break;
                case "waitUntilIsGoneFor":
                    isWebStepPassed = pageMethods.waitUntilIsGoneFor(webDriver, args[1].toString());
                    break;
                case "waitUntilIsDisplayedFor":
                    isWebStepPassed = pageMethods.waitUntilIsDisplayedFor(webDriver, args[1].toString());
                    break;
                case "waitUntilOneOfTheLocatorsIsDisplayed":
                    isWebStepPassed = (pageMethods.waitUntilOneOfTheLocatorsIsDisplayed(webDriver, args[1].toString()) != null);
                    break;
                case "waitUntilOneOfTheLocatorsIsEnabled":
                    isWebStepPassed = (pageMethods.waitUntilOneOfTheLocatorsIsEnabled(webDriver, args[1].toString()) != null);
                    break;
                case "waitUntilElementTextContains":
                    isWebStepPassed = pageMethods.waitUntilElementTextContains(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "waitUntilElementTextDoesNotContain":
                    isWebStepPassed = pageMethods.waitUntilElementTextDoesNotContain(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "enterInTextFieldFor":
                    isWebStepPassed = pageMethods.enterInTextFieldFor(webDriver, args[1].toString(), args[2].toString());
                    break;
                case "enterInTextFieldNumber":
                    isWebStepPassed = pageMethods.enterInTextFieldNumber(args[1].toString(), Integer.parseInt(args[2].toString()));
                    break;
                case "isTextPresentInWebpage":
                    isWebStepPassed = pageMethods.isTextPresentInWebpage(args[1].toString());
                    break;
                case "getAlertText":
                    return pageMethods.getAlertText();
                case "dismissAlertIfPresent":
                    isWebStepPassed = pageMethods.dismissAlertIfPresent();
                    break;
                case "acceptAlertIfPresent":
                    isWebStepPassed = pageMethods.acceptAlertIfPresent();
                    break;
                case "selectFromDropdown":
                    isWebStepPassed = pageMethods.selectFromDropdown(args[1].toString(), args[2].toString());
                    break;
                case "selectDropdownByText":
                    isWebStepPassed = pageMethods.selectDropdownByText(args[1].toString());
                    break;
                case "getTextFromLocator":
                    return pageMethods.getTextFromLocator(webDriver, args[1].toString());
                case "executeJavascript":
                    isWebStepPassed = pageMethods.executeJavascript(args[1].toString());
                    break;
                case "scrollTo":
                    isWebStepPassed = pageMethods.scrollTo(args[1].toString());
                    break;
                case "scrollToBottom":
                    isWebStepPassed = pageMethods.executeJavascript("window.scrollTo(0, document.body.scrollHeight)");
                    break;
                case "getCurrentWindowId":
                    return pageMethods.getCurrentWindowId();
                case "switchToWindowId":
                    isWebStepPassed = pageMethods.switchToWindowId(args[1].toString());
                    break;
                default:
                    throw new Exception(args[0].toString() + " method not implemented yet.");
            }
            /*
        } catch (InvocationTargetException invocationTargetException) {
            isWebStepPassed = false;
            return reportException(webDriver, args, invocationTargetException.getTargetException());
             */
        } catch (Exception ex) {
            isWebStepPassed = false;
            return reportException(webDriver, args, ex);
        }
        reportExecutionStatus(isWebStepPassed, args, webDriver);
        return isWebStepPassed;
    }

    private void reportExecutionStatus(boolean isStepPassed, Object[] args, RemoteWebDriver remoteWebDriver) {
        if (isStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            TestStepReportModel testStepReportModel = new TestStepReportModel(++SetUp.stepCounter, args, null);
            testStepReportModel.reportStepResultWithScreenshot(status, remoteWebDriver);
        }
    }

    private boolean reportException(RemoteWebDriver remoteWebDriver, Object[] args, Throwable ex) {
        if (remoteWebDriver == null) {
            ReportUtility.reportFail(getMethodStyleStepName(args) + " could not be performed.");
            ReportUtility.reportExceptionDebug(ex);
        } else {
            TestStepReportModel testStepReportModel = new TestStepReportModel(++stepCounter, args, ex);
            testStepReportModel.reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS.FAIL, remoteWebDriver);
        }
        return false;
    }

}
