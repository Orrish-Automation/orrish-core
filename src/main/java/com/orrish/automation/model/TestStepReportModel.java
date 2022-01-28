package com.orrish.automation.model;

import com.orrish.automation.utility.report.ReportUtility;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.orrish.automation.entrypoint.ReportSteps.getCurrentExtentTest;
import static com.orrish.automation.entrypoint.SetUp.playwrightPage;
import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;

public class TestStepReportModel {

    private final String[] stepNames;
    private final Exception exception;
    private final int stepCounter;

    public TestStepReportModel(int stepCounterPassed, Object[] args, Exception exceptionPassed) {
        stepNames = new String[args.length];
        int i = 0;
        for (Object eachObject : args)
            stepNames[i++] = eachObject.toString();

        exception = exceptionPassed;
        stepCounter = stepCounterPassed;
    }

    public void reportStepResultWithScreenshot(ReportUtility.REPORT_STATUS status, RemoteWebDriver driver) {
        String stepNameWithParameters = getMethodStyleStepName(stepNames);
        String reportMessage = (status == ReportUtility.REPORT_STATUS.FAIL)
                ? stepNameWithParameters + " could not be performed."
                : stepNameWithParameters + " performed successfully.";
        ReportUtility.reportWithScreenshot(driver, getTestName(), status, reportMessage);
        if (status == ReportUtility.REPORT_STATUS.FAIL) {
            if (exception != null)
                ReportUtility.reportExceptionDebug(exception);
            String pageSource = (driver != null) ? driver.getPageSource() : playwrightPage.content();
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
