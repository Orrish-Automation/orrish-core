package com.orrish.automation.entrypoint;

import com.orrish.automation.utility.report.ReportUtility;

import java.util.LinkedList;

import static com.orrish.automation.entrypoint.ReportSteps.*;
import static com.orrish.automation.entrypoint.SetUp.*;

public class TearDown {

    public TearDown() {
        try {
            stepCounter = 0;
            if (webDriver != null && webDriver.getSessionId() != null) {
                try {
                    webDriver.quit();
                    webDriver = null;
                } catch (Exception ex) {
                    ReportUtility.reportExceptionDebug(ex);
                }
            }
            if (playwright != null) {
                playwright.close();
            }
            if (playwrightBrowser != null) {
                playwrightBrowser.close();
            }
            if (appiumDriver != null && appiumDriver.getSessionId() != null) {
                try {
                    appiumDriver.quit();
                    appiumDriver = null;
                } catch (Exception ex) {
                    ReportUtility.reportExceptionDebug(ex);
                }
            }

            testList.forEach(individualTest -> {
                String testNameFromModel = individualTest.getModel().getName().replaceAll("[^A-Za-z0-9 ]", "");
                String result = individualTest.getModel().getStatus().toString();
                if (testResults.get(result) == null) {
                    LinkedList<String> newList = new LinkedList<>();
                    newList.addFirst(testNameFromModel);
                    testResults.put(result, newList);
                } else
                    testResults.get(result).addLast(testNameFromModel);
            });
            testList.clear();
            childNode = currentTest = null;
            suiteCreated = false;
        } finally {
            ReportUtility.updateReport();
        }
    }
}
