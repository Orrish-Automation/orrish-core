package com.orrish.automation.utility.verification;

import com.orrish.automation.model.VerificationResultModel;
import com.orrish.automation.utility.report.ReportUtility;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.orrish.automation.entrypoint.GeneralSteps.getMapFromString;

public class GeneralAndAPIVerifyAndReportUtility {

    public static boolean verifyValues(String responseToVerify) {
        VerificationResultModel verificationResultModel = GeneralAndAPIVerifiyUtility.verifyValues(responseToVerify);
        Map<Integer, VerificationResultModel> multiStepResult = verificationResultModel.getMultiStepResult();
        Set<Integer> keysOfSteps = multiStepResult.keySet();
        for (Integer key : keysOfSteps) {
            VerificationResultModel eachVerification = multiStepResult.get(key);
            ReportUtility.REPORT_STATUS status = (eachVerification.getOverallResult()) ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            ReportUtility.report(status, eachVerification.getVerificationResultString());
        }
        return verificationResultModel.getOverallResult();
    }

    public static boolean verifyJsons(Response actualResponse, String expectedResponseString) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.verifyJsons(actualResponse, expectedResponseString));
    }

    public static boolean isValueEqual(String node, String string1, String string2) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.isValueEqual(node, string1, string2));
    }

    public static boolean isListEqual(List<String> list1, List<String> list2) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.isListEqual(list1, list2));
    }

    public static boolean doesContain(String string1, String string2) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.doesContain(string1, string2));
    }

    public static boolean doesContainByIgnoringCase(String string1, String string2) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.doesContainByIgnoringCase(string1, string2));
    }

    public static boolean doesStartWith(String string1, String string2) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.doesStartWith(string1, string2));
    }

    public static boolean doesMatchPattern(String string1, String string2) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.doesMatchPattern(string1, string2));
    }

    public static boolean isOneOf(String string1, List<String> stringList) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.isOneOf(string1, stringList));
    }

    public static boolean isValueInIs(String value, Map values, String expectedValue) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.isValueInIs(value, values, expectedValue));
    }

    public static boolean isValueInIsNot(String value, Map values, String expectedValue) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.isValueInIsNot(value, values, expectedValue));
    }

    public static boolean verifyResponseFor(Response response, String responseToVerify) {
        return verifyResponseFor(response, null, responseToVerify);
    }

    public static boolean verifyResponseStringFor(String responseString, String responseToVerify) {
        return verifyResponseFor(null, responseString, responseToVerify);
    }

    private static boolean verifyResponseFor(Response response, String responseString, String responseToVerify) {
        Map<String, String> valueToVerify = getMapFromString(responseToVerify, "=");
        Set<String> keys = valueToVerify.keySet();
        for (String key : keys) {
            if (valueToVerify.get(key).toLowerCase().trim().contains("donotverify")) {
                ReportUtility.reportInfo("Node " + key + " is not verified as it is marked to be not verified.");
                valueToVerify.remove(key);
            }
        }
        if (valueToVerify.size() == 0)
            return true;
        VerificationResultModel verificationResultModel;
        try {
            verificationResultModel = response != null
                    ? GeneralAndAPIVerifiyUtility.verifyResponseFor(response, valueToVerify)
                    : GeneralAndAPIVerifiyUtility.verifyResponseStringFor(responseString, valueToVerify);
        } catch (Exception ex) {
            ReportUtility.reportFail("Response may be invalid.");
            ReportUtility.reportExceptionDebug(ex);
            return false;
        }
        Map<Integer, VerificationResultModel> multiStepResult = verificationResultModel.getMultiStepResult();
        Set<Integer> keysOfSteps = multiStepResult.keySet();
        for (Integer key : keysOfSteps) {
            VerificationResultModel eachVerification = multiStepResult.get(key);
            ReportUtility.REPORT_STATUS status = eachVerification.getOverallResult() ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            ReportUtility.report(status, eachVerification.getVerificationResultString());
        }
        return verificationResultModel.getOverallResult();
    }

    public static boolean verifyObjectNodeCount(Response response, String node, String count) {
        return executeAndReport(GeneralAndAPIVerifiyUtility.verifyObjectNodeCount(response, node, count));
    }

    private static boolean executeAndReport(VerificationResultModel verificationResultModel) {
        ReportUtility.REPORT_STATUS status = verificationResultModel.getOverallResult() ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
        ReportUtility.report(status, verificationResultModel.getVerificationResultString());
        return verificationResultModel.getOverallResult();
    }

}
