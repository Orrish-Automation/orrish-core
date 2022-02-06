package com.orrish.automation.entrypoint;

import com.orrish.automation.utility.report.ReportUtility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SuiteTearDown {

    public SuiteTearDown() {

        ReportUtility.finishReportPortalReport();
        Map<String, LinkedList<String>> results = ReportSteps.suiteTestResults;
        List<String> passedCases = results.get("pass") == null ? new ArrayList<>() : results.get("pass");
        List<String> failedCases = results.get("fail") == null ? new ArrayList<>() : results.get("fail");
        List<String> skippedCases = results.get("skip") == null ? new ArrayList<>() : results.get("skip");

        int totalCount = passedCases.size() + failedCases.size() + skippedCases.size();

        StringBuilder junitString = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>" +
                "<testsuites name='Test Execution Results' tests='" + totalCount + "' failures='" + failedCases.size() + "'>  " +
                "   <testsuite name='Test Suite' tests='" + totalCount + "' failures='" + failedCases.size() + "'> " +
                "  </testsuite>");
        for (String eachPass : passedCases) {
            junitString.append("<testcase classname='TestClass' name='").append(eachPass).append("' time='0'/>");
        }
        for (String eachFail : failedCases) {
            junitString.append("       <testcase classname='TestClass' name='").append(eachFail).append("' time=''>").append("        <failure type=\"NotEnoughFoo\"> Check detailed report </failure>").append("       </testcase>");
        }
        for (String eachSkip : skippedCases) {
            junitString.append("       <testcase classname='TestClass' name='").append(eachSkip).append("' time=''>").append("        <skipped type=\"NotEnoughFoo\"> Check detailed report </skipped>").append("       </testcase>");
        }

        junitString.append("</testsuites>");

        GeneralSteps.createFile("junitResult.xml", junitString.toString());

        if (junitString.toString().contains("<a "))
            System.out.println("ERROR : One of Fitnesse test names has html link preventing from generating unit report properly. Please correct it.");
    }

}
