package com.orrish.automation.utility;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

public class GeneralUtility {

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

    public static String aggregateValuesWithCommaInThem(String[] lines, int processingNodeNumber, String keyValueSeparatedBy) {
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

    public static String getRandomAlphaNumericString(int howManyCharacter) {
        return RandomStringUtils.random(howManyCharacter, true, true);
    }

    public static String[] splitWithDelimiter(String text, String delimiter) {
        return text.split(delimiter);
    }

    public static boolean waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
        }
        return true;
    }

    public static boolean evaluateMultiConditionFromString(String value) throws Exception {
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

    public static void createFile(String fileName, StringBuilder stringBuilder) {
        createFile(fileName, stringBuilder.toString());
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
        seconds = seconds - hour * 3600;
        int minute = seconds / 60;
        seconds = seconds - minute * 60;
        Map<String, Integer> valueToReturn = new HashMap<>();
        valueToReturn.put("hours", hour);
        valueToReturn.put("minutes", minute);
        valueToReturn.put("seconds", seconds);
        return valueToReturn;
    }

}
