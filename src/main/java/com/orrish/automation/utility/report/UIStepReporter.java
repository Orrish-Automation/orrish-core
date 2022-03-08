package com.orrish.automation.utility.report;

import com.orrish.automation.playwright.PlaywrightActions;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;
import static com.orrish.automation.entrypoint.ReportSteps.getCurrentExtentTest;

public class UIStepReporter {

    private final String[] stepNames;
    private final Throwable exception;
    private final int stepCounter;

    public UIStepReporter(int stepCounterPassed, Object[] args, Throwable exceptionPassed) {
        stepNames = new String[args.length];
        int i = 0;
        for (Object eachObject : args)
            stepNames[i++] = eachObject.toString();

        exception = exceptionPassed;
        stepCounter = stepCounterPassed;
    }

    public void reportStepResultWithScreenshotAndException(ReportUtility.REPORT_STATUS status, RemoteWebDriver driver) {
        String stepNameWithParameters = getMethodStyleStepName(stepNames);
        String reportMessage = (status == ReportUtility.REPORT_STATUS.FAIL)
                ? stepNameWithParameters + " could not be performed."
                : stepNameWithParameters + " performed successfully.";
        ReportUtility.reportWithScreenshot(driver, getTestName(), status, reportMessage);
        if (status == ReportUtility.REPORT_STATUS.FAIL) {
            if (exception != null)
                ReportUtility.reportExceptionDebug(exception);
            String pageSource = (driver != null) ? driver.getPageSource() : PlaywrightActions.getInstance().getPlaywrightPage().content();
            if (pageSource != null && pageSource.length() > 0) {
                ReportUtility.reportMarkupAsDebug(pageSource);
            }
        }
    }

    private String getTestName() {
        String testName = getCurrentExtentTest().getModel().getName().replace(" ", "");
        return testName + "_Step" + stepCounter;
    }
}
