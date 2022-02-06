package com.orrish.automation.entrypoint;

import com.orrish.automation.appiumselenium.AppiumPageMethods;
import com.orrish.automation.appiumselenium.SeleniumPageMethods;
import com.orrish.automation.playwright.PlaywrightActions;
import com.orrish.automation.utility.report.ReportUtility;

import java.util.LinkedList;

import static com.orrish.automation.entrypoint.ReportSteps.*;
import static com.orrish.automation.entrypoint.SetUp.stepCounter;

public class TearDown {

    public TearDown() {
        try {
            stepCounter = 0;
            SeleniumPageMethods.getInstance().quitBrowser();
            AppiumPageMethods.getInstance().quitAppOnDevice();
            PlaywrightActions.getInstance().quitPlaywright();
            appendSuiteTestResult();
        } finally {
            ReportUtility.updateReport();
        }
    }

    private void appendSuiteTestResult() {
        testList.forEach(individualTest -> {
            String testNameFromModel = individualTest.getModel().getName().replaceAll("[^A-Za-z0-9 ]", "");
            String result = individualTest.getModel().getStatus().toString();
            if (suiteTestResults.get(result) == null) {
                LinkedList<String> newList = new LinkedList<>();
                newList.addFirst(testNameFromModel);
                suiteTestResults.put(result, newList);
            } else
                suiteTestResults.get(result).addLast(testNameFromModel);
        });
        testList.clear();
        childNode = currentTest = null;
        suiteCreated = false;
    }

}
