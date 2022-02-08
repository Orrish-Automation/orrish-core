package com.orrish.automation.utility.report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.orrish.automation.entrypoint.ReportSteps;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.playwright.PlaywrightActions;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.orrish.automation.entrypoint.GeneralSteps.getMethodStyleStepName;

public class ReportUtility {

    public static ExtentTest createExtentTest(String suiteNode) {
        return ExtentReportUtility.getInstance().createTest(suiteNode);
    }

    public static void setExtentReportLocation(String location) {
        ExtentReportUtility.setExtentReportLocation(location);
    }

    public static boolean setReportPortalUrl(String url) {
        ReportPortalUtility.reportPortalBaseUrl = url;
        return true;
    }

    public static boolean setReportPortalProject(String projectName) {
        ReportPortalUtility.reportPortalProject = projectName;
        return true;
    }

    public static boolean setReportPortalApiToken(String apiKey) {
        ReportPortalUtility.reportPortalApiToken = apiKey;
        return true;
    }

    public static boolean isReportPortalEnabled() {
        return ReportPortalUtility.getInstance().isReportPortalEnabled();
    }

    public static void reportPortalStartSuite(String finalParentNameName) {
        ReportPortalUtility.getInstance().reportPortalStartSuite(finalParentNameName);
    }

    public static void reportPortalStartTest(String testNameForReportPortal) {
        ReportPortalUtility.getInstance().reportPortalStartTest(testNameForReportPortal);
    }

    public static void setReportPortalOverallTestResult(boolean value) {
        ReportPortalUtility.overallTestResult = value ? ReportPortalUtility.overallTestResult : ReportPortalUtility.REPORT_PORTAL_TEST_STATUS.failed;
    }

    private static Status getExtentStatus(REPORT_STATUS status) {
        if (status == REPORT_STATUS.PASS)
            return Status.PASS;
        if (status == REPORT_STATUS.FAIL)
            return Status.FAIL;
        if (status == REPORT_STATUS.DEBUG)
            return Status.DEBUG;
        return Status.INFO;
    }

    private static ReportPortalUtility.REPORT_PORTAL_LOG_TYPE getReportPortalStatus(REPORT_STATUS status) {
        if (status == REPORT_STATUS.PASS)
            return ReportPortalUtility.REPORT_PORTAL_LOG_TYPE.INFO;
        if (status == REPORT_STATUS.FAIL) {
            ReportPortalUtility.overallTestResult = ReportPortalUtility.REPORT_PORTAL_TEST_STATUS.failed;
            return ReportPortalUtility.REPORT_PORTAL_LOG_TYPE.ERROR;
        }
        if (status == REPORT_STATUS.DEBUG)
            return ReportPortalUtility.REPORT_PORTAL_LOG_TYPE.DEBUG;
        return ReportPortalUtility.REPORT_PORTAL_LOG_TYPE.INFO;
    }

    public static void report(REPORT_STATUS status, String message) {
        ExtentReportUtility.reportInExtent(getExtentStatus(status), message);
        if (isReportPortalEnabled()) {
            Thread thread = new Thread(() ->
                    ReportPortalUtility.getInstance().reportPortalLogStep(getReportPortalStatus(status), message));
            thread.run();
        }
    }

    public static void reportPass(String value) {
        ReportUtility.report(REPORT_STATUS.PASS, value);
    }

    public static void reportInfo(String value) {
        ReportUtility.report(REPORT_STATUS.INFO, value);
    }

    public static void reportFail(String value) {
        ReportUtility.report(REPORT_STATUS.FAIL, value);
    }

    public static void reportInfo(boolean shouldReport, String value) {
        if (!shouldReport) return;
        ReportUtility.report(REPORT_STATUS.INFO, value);
    }

    public static void reportFail(boolean shouldReport, String value) {
        if (!shouldReport) return;
        ReportUtility.report(REPORT_STATUS.FAIL, value);
    }

    public static void reportWithScreenshot(RemoteWebDriver driver, String screenshotFileName, REPORT_STATUS status, String message) {
        String reportPath = ExtentReportUtility.reportWithScreenshot(driver, screenshotFileName, getExtentStatus(status), message);
        if (isReportPortalEnabled()) {
            Thread thread = new Thread(() -> ReportPortalUtility.getInstance().reportPortalLogStepWithScreenshot(getReportPortalStatus(status), reportPath, message));
            thread.run();
        }
    }

    public static void reportWithImage(String urlOrFile, REPORT_STATUS status, String message) {
        ExtentReportUtility.reportWithImage(urlOrFile, getExtentStatus(status), message);
        if (isReportPortalEnabled()) {
            Thread thread = new Thread(() -> ReportPortalUtility.getInstance().reportPortalLogStepWithScreenshot(getReportPortalStatus(status), urlOrFile, message));
            thread.run();
        }
    }

    public static void reportExceptionDebug(Throwable throwable) {
        reportException(REPORT_STATUS.DEBUG, throwable);
    }

    public static void reportExceptionFail(Throwable throwable) {
        reportException(REPORT_STATUS.FAIL, throwable);
    }

    private static void reportException(REPORT_STATUS status, Throwable throwable) {
        ExtentReportUtility.reportException(getExtentStatus(status), throwable);
        if (isReportPortalEnabled()) {
            Thread thread = new Thread(() -> ReportPortalUtility.getInstance().reportPortalLogStep(getReportPortalStatus(status), throwable.toString()));
            thread.run();
        }
    }

    public static void reportJsonAsInfo(String text, String jsonString) {
        reportJsonAsInfo(true, text, jsonString);
    }

    public static void reportJsonAsInfo(boolean shouldReport, String text, String jsonString) {
        if (!shouldReport) return;
        ExtentReportUtility.reportJsonInExtent(text, jsonString);
        if (isReportPortalEnabled()) {
            Thread thread = new Thread(() -> ReportPortalUtility.getInstance().reportPortalLogStepWithJSON(ReportPortalUtility.REPORT_PORTAL_LOG_TYPE.INFO, text, jsonString));
            thread.run();
        }
    }

    public static boolean launchReportPortalReport(String name) {
        if (isReportPortalEnabled()) {
            Thread thread = new Thread(() -> ReportPortalUtility.getInstance().startLaunch(name.trim()));
            thread.run();
        }
        return true;
    }

    public static void updateReport() {
        ExtentReportUtility.getInstance().flush();
        if (isReportPortalEnabled()) {
            Thread threadTest = new Thread(() -> {
                //ReportPortalUtility.getInstance().reportPortalFinishTestStep(ExecutionVariables.overallTestResult);
                ReportPortalUtility.getInstance().reportPortalFinishTest(ReportPortalUtility.overallTestResult);
                ReportPortalUtility.getInstance().resetSuiteLevel();
            });
            threadTest.run();
        }
    }

    public static void reportMarkupAsInfo(boolean shouldReport, String text) {
        reportMarkup(shouldReport, REPORT_STATUS.INFO, text);
    }

    public static void reportMarkupAsDebug(String text) {
        if (SetUp.showPageInfoOnFailure)
            reportMarkup(true, REPORT_STATUS.DEBUG, text);
    }

    public static void reportMarkupAsPass(String text) {
        reportMarkup(true, REPORT_STATUS.PASS, text);
    }

    private static void reportMarkup(boolean shouldReport, REPORT_STATUS status, String text) {
        if (!shouldReport) return;
        ExtentReportUtility.reportWithMarkUp(getExtentStatus(status), text);
        if (isReportPortalEnabled()) {
            Thread thread = new Thread(() -> ReportPortalUtility.getInstance().reportPortalLogStep(getReportPortalStatus(status), text));
            thread.run();
        }
    }

    public static void resetSuite() {
        if (isReportPortalEnabled()) {
            Thread threadTest = new Thread(() -> ReportPortalUtility.getInstance().resetSuiteLevel());
            threadTest.run();
        }
    }

    public static void finishReportPortalReport() {
        if (isReportPortalEnabled()) {
            Thread threadSuite = new Thread(() -> {
                ReportPortalUtility.getInstance().reportPortalFinishSuite();
                ReportPortalUtility.getInstance().reportPortalFinishLaunch();
            });
            threadSuite.run();
        }
    }

    public static void reportFail(Object[] stepNamesPassed, RemoteWebDriver driver, int stepCounter, Exception exception) {
        report(REPORT_STATUS.FAIL, stepNamesPassed, driver, stepCounter, exception);
    }

    public static void reportInfo(String value, Object... stepNamesPassed) {
        reportInfo(getMethodStyleStepName(stepNamesPassed) + " returned \"" + value + "\"");
    }

    public static void reportInfo(Object[] stepNamesPassed, RemoteWebDriver driver, int stepCounter, Exception exception) {
        report(REPORT_STATUS.INFO, stepNamesPassed, driver, stepCounter, exception);
    }

    private static void report(REPORT_STATUS status, Object[] stepNamesPassed, RemoteWebDriver driver, int stepCounter, Exception exception) {
        String stepNameWithParameters = getMethodStyleStepName(stepNamesPassed);
        String message = stepNameWithParameters + " could not be executed successfully.";
        if (driver != null || PlaywrightActions.getInstance().isPlaywrightRunning()) {
            String testName = ReportSteps.getCurrentExtentTest().getModel().getName().replace(" ", "");
            String screenShotName = testName + "_Step" + stepCounter;
            reportWithScreenshot(driver, screenShotName, status, message);
        }
        if (exception != null) {
            reportExceptionDebug(exception);
            if (driver == null)
                return;
            String pageSource = driver.getPageSource();
            if (pageSource != null && pageSource.length() > 0) {
                reportMarkupAsDebug(pageSource);
            }
        }
    }

    public enum REPORT_STATUS {
        PASS,
        FAIL,
        INFO,
        DEBUG
    }
}
