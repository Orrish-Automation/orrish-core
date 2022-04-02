package com.orrish.automation.utility;

import java.io.*;
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
            //Remove the extra line break added above
            return value[0].substring(0, value[0].length() - 1);
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

    public static boolean deleteLineWithTextInFile(String textToFind, String fileName) {
        return actionOnTextFile("Delete", fileName, textToFind, null);
    }

    public static boolean findLineWithTextInFile(String textToFind, String fileName) {
        return actionOnTextFile("Find", fileName, textToFind, null);
    }

    public static boolean replaceTextWithInFile(String textToFind, String replacingText, String fileName) {
        return actionOnTextFile("Replace", fileName, textToFind, replacingText);
    }

    private static boolean actionOnTextFile(String action, String fileName, String textToFind, String replacingText) {

        String eachLineOfFile = "";

        String totalLines = "";
        final String[] originalContent = {""};
        try {
            List<String> originalContentList = Files.readAllLines(Paths.get(fileName));
            originalContentList.forEach(e -> originalContent[0] += e + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileReader fileReader = new FileReader(fileName);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while ((eachLineOfFile = bufferedReader.readLine()) != null) {
                if (action.contains("Find")) {
                    if (eachLineOfFile.contains(textToFind)) {
                        System.out.println("Found in file: " + fileName);
                        return true;
                    }
                } else if (action.contains("Replace"))
                    totalLines += eachLineOfFile.replace(textToFind, replacingText) + System.lineSeparator();
                else if (action.contains("Delete")) {
                    totalLines += eachLineOfFile.contains(textToFind) ? "" : eachLineOfFile + System.lineSeparator();
                }
            }
            if (action.contains("Find")) return false;
            bufferedReader.close();
            if (!originalContent[0].trim().contentEquals(totalLines.trim())) {
                // Write the new String with the replaced line OVER the same file
                FileOutputStream fileOut = new FileOutputStream(fileName);
                fileOut.write(totalLines.getBytes());
                fileOut.flush();
                fileOut.close();
                System.out.println(action + "d in file: " + fileName);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

}
