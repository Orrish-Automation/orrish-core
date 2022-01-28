package com.orrish.automation.entrypoint;

import com.aventstack.extentreports.ExtentTest;
import com.orrish.automation.utility.GeneralUtility;
import com.orrish.automation.utility.report.ExtentReportUtility;
import com.orrish.automation.utility.report.ReportPortalUtility;
import com.orrish.automation.utility.report.ReportUtility;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReportSteps {

    public static boolean suiteCreated = false;
    public static ExtentTest suiteNode;
    public static ExtentTest childNode;
    public static ExtentTest currentTest;
    public static List<ExtentTest> testList = new LinkedList<>();
    public static Map<String, LinkedList<String>> testResults = new HashMap<>();

    public static ExtentTest getCurrentExtentTest() {
        if (currentTest == null && suiteNode == null)
            setTestName("Test Run");
        return (currentTest != null)
                ? currentTest
                : (childNode == null) ? suiteNode : childNode;
    }

    public static String getCurrentTestName() {
        return getCurrentExtentTest().getModel().getName();
    }

    public static boolean setSuiteName(String suiteName) {
        suiteName = GeneralUtility.camelCaseToWords(suiteName);
        suiteCreated = true;
        if (suiteNode == null || !suiteNode.getModel().getName().equals(suiteName)) {
            suiteNode = ExtentReportUtility.getInstance().createTest(suiteName);
            if (ReportPortalUtility.getInstance().isReportPortalEnabled()) {
                String finalParentNameName = suiteName;
                Thread thread = new Thread(() -> ReportPortalUtility.getInstance().reportPortalStartSuite(finalParentNameName));
                thread.run();
            }
        }
        return true;
    }

    public static boolean setChildNode(String testName) {
        if (suiteNode == null)
            return false;
        testName = GeneralUtility.camelCaseToWords(testName);
        childNode = suiteNode.createNode(testName);
        return true;
    }

    public static boolean setTestName(String testName) {
        if (testName.trim().contains("::")) {
            String suiteName = testName.split("::")[0].trim();
            setSuiteName(suiteName);
            testName = testName.split("::")[1].trim();
        }
        testName = GeneralUtility.camelCaseToWords(testName);
        if (childNode != null) {
            currentTest = childNode.createNode(testName);
        } else if (suiteCreated) {
            currentTest = suiteNode.createNode(testName);
        } else {
            currentTest = ExtentReportUtility.getInstance().createTest(testName);
        }
        testList.add(currentTest);
        if (isReportPortalEnabled()) {
            String testNameForReportPortal = testName;
            Thread threadStartTest = new Thread(() -> {
                ReportPortalUtility.getInstance().reportPortalStartTest(testNameForReportPortal);
                //ReportPortalUtility.getInstance().reportPortalStartTestStep(testNameForReportPortal);
            }
            );
            threadStartTest.run();
        }
        return true;
    }

    public static boolean isReportPortalEnabled() {
        return ReportPortalUtility.getInstance().isReportPortalEnabled();
    }

    public boolean writeInReport(String valueToWrite) {
        ReportUtility.reportInfo(valueToWrite);
        return true;
    }

    //Effective when run after a scenario in decision table
    public boolean updateReport() {
        ReportUtility.updateReport();
        return true;
    }

    public boolean showImageWithText(String url, String text) {
        ReportUtility.reportWithImage(url, ReportUtility.REPORT_STATUS.INFO, text);
        return true;
    }

}
