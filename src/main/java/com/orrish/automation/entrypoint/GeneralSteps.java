package com.orrish.automation.entrypoint;

import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.verification.GeneralAndAPIVerifyAndReportUtility;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;

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
        if (stringToActOn == null)
            return "Target string is null.";
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

    public static boolean isOnlyDigits(String string) {
        String regex = "[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        if (string == null) {
            return false;
        }
        return pattern.matcher(string).matches();
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

    public static boolean evaluateMultiConditionFromString(String value) {
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

    public static String[] splitWithDelimiter(String text, String delimiter) {
        return text.split(delimiter);
    }

    public static String getIndexFromArray(int index, String[] array) {
        return array[index - 1];
    }

    public static String splitWithDelimiterAndReturnLastString(String text, String delimiter) {
        String[] array = splitWithDelimiter(text, delimiter);
        return array[array.length - 1];
    }

    public static boolean doesContainByIgnoringCase(String firstString, String secondString) {
        return GeneralAndAPIVerifyAndReportUtility.doesContainByIgnoringCase(firstString, secondString);
    }

    public static boolean doesStartWith(String firstString, String secondString) {
        return GeneralAndAPIVerifyAndReportUtility.doesStartWith(firstString, secondString);
    }

    public static boolean doesMatchPattern(String firstString, String secondString) {
        return GeneralAndAPIVerifyAndReportUtility.doesMatchPattern(firstString, secondString);
    }

    public static boolean isEqual(String string1, String string2) {
        return isValueEqual(null, string1, string2);
    }

    public static boolean isValueEqual(String node, String string1, String string2) {
        return GeneralAndAPIVerifyAndReportUtility.isValueEqual(node, string1, string2);
    }

    public static boolean isListEqual(List<String> list1, List<String> list2) {
        return GeneralAndAPIVerifyAndReportUtility.isListEqual(list1, list2);
    }

    public static boolean areAllValuesInListOneOf(List actualList, List expectedList) {
        for (Object value : actualList) {
            if (!expectedList.contains(String.valueOf(value))) {
                ReportUtility.reportFail(value + " is not in the expected value " + expectedList);
                return false;
            }
        }
        ReportUtility.reportPass("The values in list " + actualList + " are one of " + expectedList);
        return true;
    }

    public static boolean isOneOf(String string1, List<String> stringList) {
        return GeneralAndAPIVerifyAndReportUtility.isOneOf(string1, stringList);
    }

    public static boolean isValueInIs(String value, Map values, String expectedValue) {
        return GeneralAndAPIVerifyAndReportUtility.isValueInIs(value, values, expectedValue);
    }

    public static boolean isValueInIsNot(String value, Map values, String expectedValue) {
        return GeneralAndAPIVerifyAndReportUtility.isValueInIsNot(value, values, expectedValue);
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

    private static boolean executeShell(String command, boolean shouldReport, boolean shouldWait) {
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
    public static String getFromSystemEnvironmentVariable(String environmentVariableName) {
        return System.getenv(environmentVariableName);
    }

    public static Map<String, String> getMapFromString(String stringToConvert, String keyValueSeparatedBy) {
        Map<String, String> valueToReturn = new HashMap<>();
        String[] lines = stringToConvert.replace("\r\n", "\n").split("\n");
        if (lines.length == 1)
            lines = lines[0].split(",");
        for (int i = 0; i < lines.length; i++) {
            String eachField = lines[i];
            if (eachField.trim().contains(keyValueSeparatedBy)) {
                String key = eachField.split(keyValueSeparatedBy)[0];
                String value = aggregateValuesWithCommaInThem(lines, i, keyValueSeparatedBy);
                value = value.replace(key + keyValueSeparatedBy, "");
                valueToReturn.put(key, value);
            }
        }
        return valueToReturn;
    }

    public static String aggregateValuesWithCommaInThem(String[] lines, int processingNodeNumber, String
            keyValueSeparatedBy) {
        StringBuilder valueToReturn = new StringBuilder(lines[processingNodeNumber]);
        for (int i = processingNodeNumber + 1; i < lines.length; i++) {
            if (lines[i].contains(keyValueSeparatedBy) || lines[i].trim().length() == 0 || lines[i].trim().startsWith("</pre>"))
                return valueToReturn.toString();
            valueToReturn.append(",").append(lines[i]);
        }
        return valueToReturn.toString();
    }

    public static String camelCaseToWords(String testName) {
        testName = testName.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
        if (testName.contains("<a ")) {
            testName = testName.split("<a ")[0];
        }
        return testName.trim();
    }

    public static String getCharacterRandomAlphaNumericString(int howManyCharacter) {
        return RandomStringUtils.random(howManyCharacter, true, true);
    }

    public static boolean waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
        }
        return true;
    }

    public static String getCurrentTimeInTheFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String getTimeInTheFormatPlusDaysFromToday(String format, int days) {
        Date date = Date.from(ZonedDateTime.now().plusDays(days).toInstant());
        return getTimeInTheFormatForDate(format, date);
    }

    private static String getTimeInTheFormatForDate(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String getCurrentGMTTimeInTheFormat(String format) {
        return getGMTTimeInTheFormatForDate(format, new Date());
    }

    public static String getGMTTimeInTheFormatMinusDaysFromToday(String format, int days) {
        Date date = Date.from(ZonedDateTime.now().minusDays(days).toInstant());
        return getGMTTimeInTheFormatForDate(format, date);
    }

    public static String getGMTTimeInTheFormatPlusDaysFromToday(String format, int days) {
        Date date = Date.from(ZonedDateTime.now().plusDays(days).toInstant());
        return getGMTTimeInTheFormatForDate(format, date);
    }

    private static String getGMTTimeInTheFormatForDate(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(date);
    }

    public static String getDigitRandomNumericValue(int howManyDigits) {
        return RandomStringUtils.random(howManyDigits, false, true);
    }

    public static boolean doesContain(String value1, String value2) {
        return value1.contains(value2);
    }

    public static String getMethodStyleStepName(Object[] args) {
        String[] values = new String[args.length];
        int i = 0;
        for (Object eachObject : args)
            values[i++] = eachObject.toString();
        String methodName = values[0];

        String stepNameWithParameters = methodName + "(";
        StringBuilder stringBuilder = new StringBuilder();
        for (i = 1; i < args.length; i++)
            stringBuilder.append(", ").append(args[i]);

        stepNameWithParameters += stringBuilder + ")";
        stepNameWithParameters = stepNameWithParameters.replaceFirst(",", "");
        return stepNameWithParameters;
    }


    public static String readFile(String fileName) {
        List<String> actual;
        try {
            actual = Files.readAllLines(Paths.get(fileName));
            final String[] value = {""};
            actual.forEach(e -> value[0] += e + "\n");
            return value[0];
        } catch (IOException e) {
            return "Could not read files: " + e.getMessage();
        }
    }

    public static File createFile(String fileName, String string) {
        File file = new File(fileName);
        try {
            if (file.exists())
                file.delete();
            FileOutputStream fileWriter = new FileOutputStream(file);
            fileWriter.write(string.getBytes());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File appendFile(String fileName, String string) {
        File file = new File(fileName);
        try {
            FileOutputStream fileWriter = new FileOutputStream(file, true);
            fileWriter.write(string.getBytes());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Map<String, Integer> secondsConvertedToHHmmss(int seconds) {
        int hour = seconds / 3600;
        seconds %= 3600;
        int minute = seconds / 60;
        seconds %= 60;
        Map<String, Integer> valueToReturn = new HashMap<>();
        valueToReturn.put("hours", hour);
        valueToReturn.put("minutes", minute);
        valueToReturn.put("seconds", seconds);
        return valueToReturn;
    }

}
