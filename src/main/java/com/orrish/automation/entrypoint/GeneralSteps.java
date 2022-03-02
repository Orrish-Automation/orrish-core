package com.orrish.automation.entrypoint;

import com.orrish.automation.model.VerificationResultModel;
import com.orrish.automation.utility.GeneralUtility;
import com.orrish.automation.utility.VerifyUtility;
import com.orrish.automation.utility.report.ReportUtility;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

import static com.orrish.automation.utility.GeneralUtility.getMapFromString;

public class GeneralSteps {

    public static boolean conditionalStep = true;

    public boolean setConditionalValueForNextStep(String valuePassed) {
        try {
            conditionalStep = Boolean.parseBoolean(valuePassed);
        } catch (Exception ex) {
            String[] values = valuePassed.split("=");
            if (values.length != 2) {
                ReportUtility.reportFail("Condition that you entered is not valid. It has to be either boolean or equality check. e.g. name=test");
                return false;
            }
            conditionalStep = values[0].trim().equals(values[1].trim());
        }
        return true;
    }

    public boolean resetConditionalValueForNextStep() {
        return conditionalStep = true;
    }

    public String echo(String value) {
        return value;
    }

    public boolean downloadFromUrlAndSaveAs(String url, String fileName) {
        return executeAndReport(VerifyUtility.downloadFromUrlAndSaveAs(url, fileName));
    }

    public String replaceStringWithIn(String valueToFind, String valueToReplace, String stringToActOn) {
        if (!conditionalStep) return "";
        if (stringToActOn == null)
            return "Target string is null.";
        stringToActOn = stringToActOn.replace(valueToFind, valueToReplace);
        return stringToActOn;
    }

    public String getValidStringBetweenAnd(String firstString, String secondString) {
        if (!conditionalStep) return "";
        if (firstString == null && secondString == null)
            return "";
        if (firstString == null || firstString.toLowerCase().trim().startsWith("donotmodify"))
            return secondString;
        if (secondString == null || secondString.toLowerCase().trim().startsWith("donotmodify"))
            return firstString;
        return firstString;
    }

    public String concatenateAnd(String string1, String string2) {
        if (!conditionalStep) return "";
        String value = String.valueOf(string1).trim() + String.valueOf(string2).trim();
        ReportUtility.reportInfo("Concatenated value is: " + value);
        return value;
    }

    public int subtractFrom(int a, int b) {
        if (!conditionalStep) return 0;
        int c = b - a;
        ReportUtility.reportInfo(a + "subtracted from " + b + " is: " + c);
        return c;
    }

    public boolean isOnlyDigits(String string) {
        return executeAndReport(VerifyUtility.isOnlyDigits(string));
    }

    public String getSumOfIntegerValuesInList(List<String> stringValues) {
        if (!conditionalStep) return "";
        int calculatedTotalSpend = 0;
        for (String eachValue : stringValues) {
            calculatedTotalSpend += Integer.parseInt(eachValue);
        }
        return Integer.toString(calculatedTotalSpend);
    }

    public String getSumOfDecimalValuesInList(List<String> stringValues) {
        if (!conditionalStep) return "";
        double calculatedTotalSpend = 0.00;
        for (String eachValue : stringValues) {
            calculatedTotalSpend += Double.parseDouble(eachValue);
        }
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        ReportUtility.reportInfo(df2.format(calculatedTotalSpend) + " is the calculated value.");
        return df2.format(calculatedTotalSpend);
    }

    public boolean evaluateMultiConditionFromString(String value) {
        if (!conditionalStep) return true;
        try {
            List<String> values = new ArrayList(Arrays.asList(value.trim().split(" ")));
            values.removeIf(a -> a.trim().length() == 0);
            boolean computedValueToReturn = values.get(0).toLowerCase().contains("pass") || values.get(0).toLowerCase().contains("true");
            for (int i = 1; i < values.size(); i = i + 2) {
                boolean currentValue = values.get(i + 1).toLowerCase().contains("pass") || values.get(i + 1).toLowerCase().contains("true");
                if (values.get(i).trim().toLowerCase().startsWith("and")) {
                    computedValueToReturn &= currentValue;
                } else if (values.get(i).trim().toLowerCase().startsWith("or")) {
                    computedValueToReturn |= currentValue;
                } else throw new Exception("Please check the syntax.");
            }
            return computedValueToReturn;
        } catch (Exception ex) {
            ReportUtility.reportExceptionFail(ex);
            return false;
        }
    }

    public String[] splitWithDelimiter(String text, String delimiter) {
        if (!conditionalStep) return new String[]{};
        return text.split(delimiter);
    }

    public String getIndexFromArray(int index, String[] array) {
        if (!conditionalStep) return "";
        return array[index - 1];
    }

    public String splitWithDelimiterAndReturnLastString(String text, String delimiter) {
        if (!conditionalStep) return "";
        String[] array = splitWithDelimiter(text, delimiter);
        return array[array.length - 1];
    }

    public boolean doesContain(String string1, String string2) {
        return executeAndReport(VerifyUtility.doesContain(string1, string2));
    }

    public boolean doesContainByIgnoringCase(String string1, String string2) {
        return executeAndReport(VerifyUtility.doesContainByIgnoringCase(string1, string2));
    }

    public boolean doesStartWith(String string1, String string2) {
        return executeAndReport(VerifyUtility.doesStartWith(string1, string2));
    }

    public boolean doesMatchPattern(String string1, String string2) {
        return executeAndReport(VerifyUtility.doesMatchPattern(string1, string2));
    }

    public boolean isEqual(String string1, String string2) {
        if (!conditionalStep) return true;
        return isValueEqual(null, string1, string2);
    }

    public boolean isEqualWithoutValidation(String string1, String string2) {
        if (!conditionalStep) return true;
        boolean isSame = string1.trim().equals(string2.trim());
        ReportUtility.reportInfo("String \"" + string1 + "\" and \"" + string2 + "\" are " + (isSame ? "" : " not ") + "same.");
        return isSame;
    }

    public boolean isValueEqual(String node, String string1, String string2) {
        return executeAndReport(VerifyUtility.isValueEqual(node, string1, string2));
    }

    public boolean isListEqual(List<String> list1, List<String> list2) {
        return executeAndReport(VerifyUtility.isListEqual(list1, list2));
    }

    public boolean areAllValuesInListOneOf(List actualList, List expectedList) {
        return executeAndReport(VerifyUtility.areAllValuesInListOneOf(actualList, expectedList));
    }

    public boolean isOneOf(String string1, List<String> stringList) {
        return executeAndReport(VerifyUtility.isOneOf(string1, stringList));
    }

    public boolean isValueInIs(String value, Map values, String expectedValue) {
        return executeAndReport(VerifyUtility.isValueInIs(value, values, expectedValue));
    }

    public boolean isValueInIsNot(String value, Map values, String expectedValue) {
        return executeAndReport(VerifyUtility.isValueInIsNot(value, values, expectedValue));
    }

    public boolean executeShell(String cmd) {
        return executeShell(cmd, true, true);
    }

    public boolean executeShellWithoutReporting(String cmd) {
        return executeShell(cmd, false, true);
    }

    public boolean executeShellWithoutWait(String cmd) {
        return executeShell(cmd, false, false);
    }

    private boolean executeShell(String command, boolean shouldReport, boolean shouldWait) {
        if (!conditionalStep) return true;
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (shouldWait) {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                process.waitFor();
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = inputReader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                while ((line = errorReader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                if (shouldReport) {
                    ReportUtility.reportMarkupAsPass("Command :" + command + "\nOutput:" + output);
                }
            } else {
                ReportUtility.reportMarkupAsPass("Command :" + command + " may have been executed. Since there was no wait for that process, it is not sure whether the command was successful.");
            }
        } catch (Exception ex) {
            ReportUtility.reportExceptionFail(ex);
            return false;
        }
        return true;
    }

    //If you want to get sensitive information etc. from environment variable. Example: cloud provider API key etc.
    public String getFromSystemEnvironmentVariable(String environmentVariableName) {
        if (!conditionalStep) return "";
        return System.getenv(environmentVariableName);
    }

    public boolean verifyValues(String responseToVerify) {
        if (!conditionalStep) return true;
        VerificationResultModel verificationResultModel = VerifyUtility.verifyValues(responseToVerify);
        Map<Integer, VerificationResultModel> multiStepResult = verificationResultModel.getMultiStepResult();
        Set<Integer> keysOfSteps = multiStepResult.keySet();
        for (Integer key : keysOfSteps) {
            VerificationResultModel eachVerification = multiStepResult.get(key);
            ReportUtility.REPORT_STATUS status = (eachVerification.getOverallResult()) ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            ReportUtility.report(status, eachVerification.getVerificationResultString());
        }
        return verificationResultModel.getOverallResult();
    }

    public boolean verifyJsons(Response actualResponse, String expectedResponseString) {
        return executeAndReport(VerifyUtility.verifyJsons(actualResponse, expectedResponseString));
    }

    public boolean verifyResponseFor(Response response, String responseToVerify) {
        return verifyResponseFor(response, null, responseToVerify);
    }

    public boolean verifyResponseStringFor(String responseString, String responseToVerify) {
        return verifyResponseFor(null, responseString, responseToVerify);
    }

    private boolean verifyResponseFor(Response response, String responseString, String responseToVerify) {
        if (!conditionalStep) return true;
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
                    ? VerifyUtility.verifyResponseFor(response, valueToVerify)
                    : VerifyUtility.verifyResponseStringFor(responseString, valueToVerify);
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

    public boolean verifyObjectNodeCount(Response response, String node, String count) {
        return executeAndReport(VerifyUtility.verifyObjectNodeCount(response, node, count));
    }

    private boolean executeAndReport(VerificationResultModel verificationResultModel) {
        if (!conditionalStep) return true;
        ReportUtility.REPORT_STATUS status = verificationResultModel.getOverallResult() ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
        ReportUtility.report(status, verificationResultModel.getVerificationResultString());
        return verificationResultModel.getOverallResult();
    }

    public String getCharacterRandomAlphaNumericString(int howManyCharacter) {
        if (!conditionalStep) return "";
        return RandomStringUtils.random(howManyCharacter, true, true);
    }

    public static boolean waitSeconds(int seconds) {
        if (!conditionalStep) return true;
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
        }
        return true;
    }

    public String getCurrentTimeInTheFormat(String format) {
        if (!conditionalStep) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public String getTimeInTheFormatPlusDaysFromToday(String format, int days) {
        if (!conditionalStep) return "";
        Date date = Date.from(ZonedDateTime.now().plusDays(days).toInstant());
        return getTimeInTheFormatForDate(format, date);
    }

    private String getTimeInTheFormatForDate(String format, Date date) {
        if (!conditionalStep) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public String getCurrentGMTTimeInTheFormat(String format) {
        return getGMTTimeInTheFormatForDate(format, new Date());
    }

    public String getGMTTimeInTheFormatMinusDaysFromToday(String format, int days) {
        Date date = Date.from(ZonedDateTime.now().minusDays(days).toInstant());
        return getGMTTimeInTheFormatForDate(format, date);
    }

    public String getGMTTimeInTheFormatPlusDaysFromToday(String format, int days) {
        Date date = Date.from(ZonedDateTime.now().plusDays(days).toInstant());
        return getGMTTimeInTheFormatForDate(format, date);
    }

    private String getGMTTimeInTheFormatForDate(String format, Date date) {
        if (!conditionalStep) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(date);
    }

    public String getDigitRandomNumericValue(int howManyDigits) {
        if (!conditionalStep) return "";
        return RandomStringUtils.random(howManyDigits, false, true);
    }

    public long getCurrentEpochTime() {
        if (!conditionalStep) return 0;
        Instant instant = Instant.now();
        return instant.getEpochSecond();
    }

    public long getCurrentEpochTimeAndAppendZeros() {
        return getCurrentEpochTime() * 1000;
    }

    public String readFile(String fileName) {
        if (!conditionalStep) return "";
        return GeneralUtility.readFile(fileName);
    }

    public static File createFileWithContent(String fileName, String string) {
        if (!conditionalStep) return null;
        return GeneralUtility.createFile(fileName, string);
    }

    public File appendFileWithContent(String fileName, String string) {
        if (!conditionalStep) return null;
        return GeneralUtility.appendFile(fileName, string);
    }

    public boolean replaceTextWithInFile(String textToFind, String replacingText, String filePath) {
        if (!conditionalStep) return true;
        return GeneralUtility.replaceTextWithInFile(textToFind, replacingText, filePath);
    }

    public boolean deleteLineWithTextInFile(String textToFind, String filePath) {
        if (!conditionalStep) return true;
        return GeneralUtility.deleteLineWithTextInFile(textToFind, filePath);
    }

    public boolean findLineWithTextInFile(String textToFind, String filePath) {
        if (!conditionalStep) return true;
        return GeneralUtility.findLineWithTextInFile(textToFind, filePath);
    }

    public Map<String, Integer> secondsConvertedToHHmmss(int seconds) {
        if (!conditionalStep) return null;
        return GeneralUtility.secondsConvertedToHHmmss(seconds);
    }

}
