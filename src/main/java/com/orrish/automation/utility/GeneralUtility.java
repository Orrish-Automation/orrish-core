package com.orrish.automation.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralUtility {

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

}
