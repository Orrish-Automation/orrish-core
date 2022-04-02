package com.orrish.automation.entrypoint;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

import static com.orrish.automation.utility.GeneralUtility.deleteLineWithTextInFile;
import static com.orrish.automation.utility.GeneralUtility.replaceTextWithInFile;

public class Administration {

    static java.util.Set<String> allFileNameList = new HashSet<>();
    static Map<String, ArrayList<Integer>> foundFileNameList = new HashMap<>();
    private static String textToAppend = "";

    public static void main(String[] args) {
        if (args.length == 0) {
            printInputError();
        } else if (args[0].contentEquals("findExact")) {
            String textFindFolderLocation = args[1];
            String stringToFind = args[2];
            getAllFileNames(allFileNameList, Paths.get(textFindFolderLocation));
            Predicate<String> stringPredicate = p -> !p.endsWith(".wiki");
            allFileNameList.removeIf(stringPredicate);
            findFilesWithMethod(textFindFolderLocation, stringToFind, false);
        } else if (args[0].contentEquals("findMethod")) {
            String textFindFolderLocation = args[1];
            String methodName = args[2];
            getAllFileNames(allFileNameList, Paths.get(textFindFolderLocation));
            Predicate<String> stringPredicate = p -> !p.endsWith(".wiki");
            allFileNameList.removeIf(stringPredicate);
            findFilesWithMethod(textFindFolderLocation, methodName, true);
        } else if (args[0].contentEquals("deleteExact")) {
            String textFindFolderLocation = args[1];
            String textToFind = args[2];
            getAllFileNames(allFileNameList, Paths.get(textFindFolderLocation));
            Predicate<String> stringPredicate = p -> !p.endsWith(".wiki");
            allFileNameList.removeIf(stringPredicate);
            for (String fileName : allFileNameList) {
                deleteLineWithTextInFile(textToFind, fileName);
            }
        } else if (args[0].contentEquals("replaceExact")) {
            String textFindReplaceFolderLocation = args[1];
            String textToFind = args[2];
            String textToReplace = args[3];
            getAllFileNames(allFileNameList, Paths.get(textFindReplaceFolderLocation));
            Predicate<String> stringPredicate = p -> !p.endsWith(".wiki");
            allFileNameList.removeIf(stringPredicate);
            for (String fileName : allFileNameList) {
                replaceTextWithInFile(textToFind, textToReplace, fileName);
            }
        } else if (args[0].contentEquals("replaceMethod")) {
            String methodName = args[2].trim();
            String textFindReplaceFolderLocation = args[1];
            findFilesWithMethod(textFindReplaceFolderLocation, methodName, true);
            String replacingString = args[3].trim();
            replaceMethodInFiles(replacingString);
        } else {
            printInputError();
        }
    }

    private static void findFilesWithMethod(String textFindReplaceFolderLocation, String methodName, boolean isMethod) {

        getAllFileNames(allFileNameList, Paths.get(textFindReplaceFolderLocation));
        Predicate<String> stringPredicate = p -> !p.endsWith(".wiki");
        allFileNameList.removeIf(stringPredicate);

        for (String fileName : allFileNameList) {
            File file = new File(fileName);
            int lineNum = 0;
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    ++lineNum;
                    boolean isFound = isMethod ? getMethodNameFromStep(line).equals(methodName.startsWith("|") ? getMethodNameFromStep(methodName) : methodName) : line.contains(methodName);
                    if (isFound) {
                        //Found line with the desired method
                        ArrayList<Integer> lines = foundFileNameList.get(fileName);
                        if (lines == null)
                            lines = new ArrayList<>();
                        lines.add(lineNum);
                        foundFileNameList.put(fileName, lines);
                        textToAppend += fileName + " Line " + lineNum + "\n";
                    }
                }

            } catch (Exception e) {
            }
        }
        System.out.println(textToAppend);
        System.out.println("Files found : " + foundFileNameList.size());
    }

    private static void printInputError() {

        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_BLUE = "\u001B[34m";
        final String BOLD_TEXT = "\033[0;1m";

        System.out.println(BOLD_TEXT + ANSI_PURPLE + "Pass argument in one of the formats below. Use proper jar file name." + ANSI_RESET + System.lineSeparator() +
                ANSI_YELLOW + "To find exact text in FitNesse pages: " + ANSI_RESET + ANSI_BLUE + "java -jar <jar_rile>.jar findExact directoryToWorkOn textToFind" + ANSI_RESET + System.lineSeparator() +
                ANSI_YELLOW + "To find method in FitNesse pages: " + ANSI_RESET + ANSI_BLUE + "java -jar <jar_rile>.jar findMethod directoryToWorkOn methodToFind" + ANSI_RESET + System.lineSeparator() +
                BOLD_TEXT + ANSI_YELLOW + "     Example: " + ANSI_BLUE + "java -jar <jar_file>.jar findMethod '/Users/user/FitNesseRoot' '|ensure|Verify response for|status=SUCCESS|' " + ANSI_RESET + System.lineSeparator() +
                ANSI_YELLOW + "To delete lines with exact text in FitNesse pages: " + ANSI_RESET + ANSI_BLUE + "java -jar <jar_rile>.jar deleteExact directoryToWorkOn textToDelete" + ANSI_RESET + System.lineSeparator() +
                BOLD_TEXT + ANSI_YELLOW + "     Example: " + ANSI_BLUE + "java -jar <jar_file>.jar deleteExact '/Users/user/FitNesseRoot' 'hello' " + ANSI_RESET + System.lineSeparator() +
                ANSI_YELLOW + "To replace method in FitNesse pages: " + ANSI_RESET + ANSI_BLUE + "java -jar <jar_rile>.jar replaceMethod directoryToWorkOn textToFind textToReplace" + ANSI_RESET + System.lineSeparator() +
                BOLD_TEXT + ANSI_YELLOW + "     Example: " + ANSI_BLUE + "java -jar <jar_file>.jar replaceMethod '/Users/user/FitNesseRoot' '|ensure|Verify response for|status=SUCCESS|' '|ensure|Verify responses|status=SUCCESS|'" + ANSI_RESET + System.lineSeparator() +
                ANSI_YELLOW + "To replace exact text in FitNesse pages: " + ANSI_RESET + ANSI_BLUE + "java -jar <jar_file>.jar replaceExact directoryToWorkOn textToFind textToReplace" + ANSI_RESET + System.lineSeparator() +
                BOLD_TEXT + ANSI_YELLOW + "     Example: " + ANSI_BLUE + "java -jar <jar_file>.jar replaceExact '/Users/user/FitNesseRoot' textToFind textToReplace" + ANSI_RESET);
    }

    private static String getMethodNameFromStep(String textPassed) {
        if (!textPassed.startsWith("|"))
            return textPassed;
        List<String> methodSplit = Arrays.asList(textPassed.split("\\|"));
        int counter = 0;
        String firstColumn = "";
        while ((firstColumn.contains("ensure") || firstColumn.startsWith("$") || firstColumn.isEmpty()) && counter < methodSplit.size()) {
            try {
                firstColumn = methodSplit.get(++counter).trim();
            } catch (ArrayIndexOutOfBoundsException ex) {
                ++counter;
            }
        }
        String methodNameToReturn = "";
        for (int i = counter; i < methodSplit.size(); i += 2) {
            List<String> currentCellWords = Arrays.asList(methodSplit.get(i).trim().split(" "));
            for (int j = 0; j < currentCellWords.size(); j++) {
                if (!currentCellWords.get(j).trim().isEmpty()) {
                    methodNameToReturn += StringUtils.capitalize(currentCellWords.get(j).trim());
                }
            }
        }
        methodNameToReturn = StringUtils.uncapitalize(methodNameToReturn);
        return methodNameToReturn;
    }

    private static void replaceMethodInFiles(String replacingText) {

        String eachLineOfFile = "";

        Set<String> foundFileNames = foundFileNameList.keySet();
        for (String fileName : foundFileNames) {
            String totalLines = "";
            try (FileReader fileReader = new FileReader(fileName);
                 BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                int fileLineCounter = 0;
                int lineOccurrenceCounter = 0;
                Object[] lineNumbers = foundFileNameList.get(fileName).toArray();
                Arrays.sort(lineNumbers);
                while ((eachLineOfFile = bufferedReader.readLine()) != null) {
                    if (lineOccurrenceCounter < lineNumbers.length && ++fileLineCounter == Integer.parseInt(lineNumbers[lineOccurrenceCounter].toString())) {
                        if (replacingText.trim().length() == 0) {
                            continue;
                        } else {
                            ++lineOccurrenceCounter;
                            Map<Integer, String> toFind = textToReplaceWith(eachLineOfFile);
                            Set<Map.Entry<Integer, String>> toFindEntrySet = toFind.entrySet();
                            Map<Integer, String> toReplaceText = textToReplaceWith(replacingText);
                            Object[] toReplaceValues = toReplaceText.values().toArray();
                            int lastIndex = 0;
                            int replaceCounter = 0;
                            String newLine = "";
                            for (Map.Entry<Integer, String> toFindEachEntry : toFindEntrySet) {
                                int currentIndex = toFindEachEntry.getKey();
                                if (lastIndex != currentIndex)
                                    newLine += eachLineOfFile.substring(lastIndex, currentIndex);
                                newLine += toReplaceValues[replaceCounter++] + "|";
                                lastIndex = currentIndex + toFindEachEntry.getValue().length();
                                try {
                                    //Copy the next column
                                    String currentColumnValue = eachLineOfFile.substring(lastIndex).split("\\|")[1];
                                    lastIndex += currentColumnValue.length();
                                    newLine += currentColumnValue + "|";
                                } catch (ArrayIndexOutOfBoundsException ex) {
                                }
                            }
                            if (toReplaceValues.length - toFindEntrySet.size() == 1) {
                                newLine += toReplaceValues[toReplaceValues.length - 1] + "|";
                            }
                            newLine += (!newLine.endsWith("|")) ? "|" : "";
                            eachLineOfFile = newLine;
                        }
                    }
                    totalLines += eachLineOfFile + System.lineSeparator();
                }
                bufferedReader.close();
                // Write the new String with the replaced line OVER the same file
                FileOutputStream fileOut = new FileOutputStream(fileName);
                fileOut.write(totalLines.getBytes());
                fileOut.flush();
                fileOut.close();
            } catch (Exception e) {
                System.out.println(fileName);
            }
        }
    }

    private static Map<Integer, String> textToReplaceWith(String currentLine) {
        String[] allColumnsOfCurrentLine = currentLine.split("\\|");
        int counter = 0;
        int index = 0;
        TreeMap<Integer, String> valueToReturn = new TreeMap<>();
        do {
            for (String eachValue : allColumnsOfCurrentLine) {
                if (!(eachValue.trim().isEmpty() || eachValue.trim().startsWith("ensure") || eachValue.trim().startsWith("$"))) {
                    break;
                }
                index += (eachValue.length() + 1);
                ++counter;
            }
            for (int i = counter; i < allColumnsOfCurrentLine.length; i++) {
                if ((i - counter) % 2 == 0)
                    valueToReturn.put(index, allColumnsOfCurrentLine[i]);
                index += allColumnsOfCurrentLine[i].length();
            }
        } while (++counter < allColumnsOfCurrentLine.length);
        return valueToReturn;
    }


    private static java.util.Set<String> getAllFileNames(java.util.Set<String> fileNames, Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getAllFileNames(fileNames, path);
                } else {
                    fileNames.add(path.toAbsolutePath().toString());
                }
            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
        }
        return fileNames;
    }

}
