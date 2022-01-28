package com.orrish.automation.entrypoint;

import com.orrish.automation.utility.GeneralUtility;
import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.verification.VerifyAndReportUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class GeneralSteps {

    public static String echo(String value) {
        return value;
    }

    public static boolean downloadFromUrlAndSaveAs(String url, String fileName) {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(fileName));
        } catch (IOException e) {
            ReportUtility.reportFail(url + " could not be saved. " + e.getMessage());
            return false;
        }
        ReportUtility.reportPass(url + " successfully saved with filename " + fileName);
        return true;
    }

    public static String replaceStringWithIn(String valueToFind, String valueToReplace, String stringToActOn) {
        stringToActOn = stringToActOn.replace(valueToFind, valueToReplace);
        return stringToActOn;
    }

    public static String getValidStringBetweenAnd(String firstString, String secondString) {
        if (firstString == null && secondString == null)
            return "";
        if (firstString == null || firstString.toLowerCase().trim().startsWith("donotmodify"))
            return secondString;
        if (secondString == null || secondString.toLowerCase().trim().startsWith("donotmodify"))
            return firstString;
        return secondString;
    }

    public static String concatenateAnd(String string1, String string2) {
        return string1.trim() + string2.trim();
    }

    public static int subtractFrom(int a, int b) {
        int c = a - b;
        ReportUtility.reportInfo(a + "subtracted from " + b + " is: " + c);
        return c;
    }

    public static String getSumOfIntegerValuesInList(List<String> stringValues) {
        int calculatedTotalSpend = 0;
        for (String eachValue : stringValues) {
            calculatedTotalSpend += Integer.parseInt(eachValue);
        }
        return Integer.toString(calculatedTotalSpend);
    }

    public static String getSumOfDecimalValuesInList(List<String> stringValues) {
        double calculatedTotalSpend = 0.00;
        for (String eachValue : stringValues) {
            calculatedTotalSpend += Double.parseDouble(eachValue);
        }
        DecimalFormat df2 = new DecimalFormat("#,###.00");
        ReportUtility.reportInfo(df2.format(calculatedTotalSpend) + " is the calculated value.");
        return df2.format(calculatedTotalSpend);
    }

    public static String getCharacterRandomAlphaNumericString(int howManyCharacter) {
        return GeneralUtility.getRandomAlphaNumericString(howManyCharacter);
    }

    public static String getDigitRandomNumericValue(int howManyDigits) {
        return GeneralUtility.getDigitRandomNumericValue(howManyDigits);
    }

    public static String getCurrentTimeInTheFormat(String format) {
        return GeneralUtility.getCurrentTimeInTheFormat(format);
    }

    public static String getTimeInTheFormatPlusDaysFromToday(String format, int day) {
        return GeneralUtility.getTimeInTheFormatPlusDaysFromToday(format, day);
    }

    public static String getCurrentGMTTimeInTheFormat(String format) {
        return GeneralUtility.getCurrentGMTTimeInTheFormat(format);
    }

    public static String getGMTTimeInTheFormatMinusDaysFromToday(String format, int day) {
        return GeneralUtility.getGMTTimeInTheFormatMinusDaysFromToday(format, day);
    }

    public static String getGMTTimeInTheFormatPlusDaysFromToday(String format, int day) {
        return GeneralUtility.getGMTTimeInTheFormatPlusDaysFromToday(format, day);
    }

    public static boolean evaluateMultiConditionFromString(String value) {
        try {
            return GeneralUtility.evaluateMultiConditionFromString(value);
        } catch (Exception ex) {
            ReportUtility.reportExceptionFail(ex);
            return false;
        }
    }

    public static boolean waitSeconds(int seconds) {
        return GeneralUtility.waitSeconds(seconds);
    }

    public static String[] splitWithDelimiter(String text, String delimiter) {
        return GeneralUtility.splitWithDelimiter(text, delimiter);
    }

    public static String getIndexFromArray(int index, String[] array) {
        return array[index - 1];
    }

    public static String splitWithDelimiterAndReturnLastString(String text, String delimiter) {
        String[] array = splitWithDelimiter(text, delimiter);
        return array[array.length - 1];
    }

    public static boolean doesContain(String firstString, String secondString) {
        return VerifyAndReportUtility.doesContain(firstString, secondString);
    }

    public static boolean doesContainByIgnoringCase(String firstString, String secondString) {
        return VerifyAndReportUtility.doesContainByIgnoringCase(firstString, secondString);
    }

    public static boolean doesStartWith(String firstString, String secondString) {
        return VerifyAndReportUtility.doesStartWith(firstString, secondString);
    }

    public static boolean doesMatchPattern(String firstString, String secondString) {
        return VerifyAndReportUtility.doesMatchPattern(firstString, secondString);
    }

    public static boolean isEqual(String string1, String string2) {
        return isValueEqual(null, string1, string2);
    }

    public static boolean isValueEqual(String node, String string1, String string2) {
        return VerifyAndReportUtility.isValueEqual(node, string1, string2);
    }

    public static boolean isListEqual(List<String> list1, List<String> list2) {
        return VerifyAndReportUtility.isListEqual(list1, list2);
    }

    public static boolean isOneOf(String string1, List<String> stringList) {
        return VerifyAndReportUtility.isOneOf(string1, stringList);
    }

    public static boolean isValueInIs(String value, Map values, String expectedValue) {
        return VerifyAndReportUtility.isValueInIs(value, values, expectedValue);
    }

    public static boolean isValueInIsNot(String value, Map values, String expectedValue) {
        return VerifyAndReportUtility.isValueInIsNot(value, values, expectedValue);
    }

    public static long getCurrentEpochTime() {
        Instant instant = Instant.now();
        return instant.getEpochSecond();
    }

    public static long getCurrentEpochTimeAndAppendZeros() {
        return getCurrentEpochTime() * 1000;
    }

    public static boolean executeShell(String cmd) {
        return executeShell(cmd, true, true);
    }

    public static boolean executeShellWithoutReporting(String cmd) {
        return executeShell(cmd, false, true);
    }

    public static boolean executeShellWithoutWait(String cmd) {
        return executeShell(cmd, false, false);
    }

    private static boolean executeShell(String cmd, boolean shouldReport, boolean shouldWait) {
        StringBuilder output = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            if (shouldWait) {
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                if (shouldReport)
                    ReportUtility.reportMarkupAsPass("Command :" + cmd + "\nOutput:" + output);
            }
        } catch (Exception ex) {
            ReportUtility.reportExceptionFail(ex);
            return false;
        }
        return true;
    }

}
