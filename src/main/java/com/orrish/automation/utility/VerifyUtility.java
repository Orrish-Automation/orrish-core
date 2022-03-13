package com.orrish.automation.utility;

import com.orrish.automation.model.VerificationResultModel;
import io.restassured.response.Response;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static io.restassured.path.json.JsonPath.from;

public class VerifyUtility {

    public static VerificationResultModel verifyValues(String verificationPairs) {
        Map<String, String> valueToVerify = GeneralUtility.getMapFromString(verificationPairs, "=");
        Map<Integer, VerificationResultModel> eachVerificationResult = new HashMap<>();
        Set<String> keys = valueToVerify.keySet();
        boolean finalVerificationResult = true;
        int counter = 0;
        for (String eachKey : keys) {
            if (eachKey.equals(valueToVerify.get(eachKey))) {
                eachVerificationResult.put(++counter, new VerificationResultModel(true, eachKey + " is validated successfully."));
            } else {
                eachVerificationResult.put(++counter, new VerificationResultModel(false, eachKey + " is not equal to " + valueToVerify.get(eachKey)));
                finalVerificationResult = false;
            }
        }
        if (finalVerificationResult)
            return new VerificationResultModel(true, eachVerificationResult);
        return new VerificationResultModel(false, eachVerificationResult);
    }

    public static VerificationResultModel areAllValuesInListOneOf(List actualList, List expectedList) {
        for (Object value : actualList) {
            if (!expectedList.contains(String.valueOf(value))) {
                return new VerificationResultModel(false, value + " is not in the expected value " + expectedList);
            }
        }
        return new VerificationResultModel(true, "The values in list " + actualList + " are one of " + expectedList);
    }

    public static VerificationResultModel isOnlyDigits(String string) {
        String regex = "[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        boolean valueToReturn = (string == null) ? false : pattern.matcher(string).matches();
        String message = valueToReturn ? string + " contains only digits." : string + " has some characters which are not digits.";
        return new VerificationResultModel(valueToReturn, message);
    }

    public static VerificationResultModel isValueEqual(String node, String string1, String string2) {
        String passString = "Values you compared are equal : ";
        if ((string1 == null && string2 == null)) {
            return new VerificationResultModel(true, passString + " Both are null.");
        }
        if ((String.valueOf(string1).equals(String.valueOf(string2)))) {
            return new VerificationResultModel(true, passString + "string1 is " + string1 + " and string2 is " + string2);
        }
        String failString = (node == null) ?
                "Value you entered did not match. First string: '%s' Second string: '%s'"
                : node + " value you entered did not match. First string: '%s' Second string: '%s'";
        if (string1 == null || string2 == null) {
            return new VerificationResultModel(false, String.format(failString, string1, string2));
        }
        return verifyConditionAndReturn(string1.equals(string2),
                passString + string1,
                String.format(failString, string1, string2));
    }

    public static VerificationResultModel isListEqual(List<String> list1, List<String> list2) {
        Collections.sort(list1);
        Collections.sort(list2);
        return verifyConditionAndReturn(list1.equals(list2),
                "Values you compared are equal : " + list1,
                "Value you entered did not match. First list: '" + list1 + "' Second list: '" + list2 + "'");
    }

    public static VerificationResultModel doesContain(String string1, String string2) {
        return verifyConditionAndReturn(String.valueOf(string1).contains(String.valueOf(string2)),
                string1 + " contains " + string2,
                string1 + " does not contain " + string2);
    }

    public static VerificationResultModel doesContainByIgnoringCase(String string1, String string2) {
        return verifyConditionAndReturn(String.valueOf(string1).toLowerCase().contains(String.valueOf(string2).toLowerCase()),
                string1 + " contains " + string2,
                string1 + " does not contain " + string2);
    }

    public static VerificationResultModel doesStartWith(String string1, String string2) {
        return verifyConditionAndReturn(String.valueOf(string1).startsWith(String.valueOf(string2)),
                string1 + " starts with " + string2,
                string1 + " does not start with " + string2);
    }

    public static VerificationResultModel doesMatchPattern(String string1, String string2) {
        String regex = string2;
        Pattern pattern = Pattern.compile(regex);
        boolean valueToReturn = pattern.matcher(string1).matches();
        return verifyConditionAndReturn(valueToReturn,
                string1 + " matches pattern " + string2,
                string1 + " does not match pattern " + string2);
    }

    public static VerificationResultModel isOneOf(String string1, List<String> stringList) {
        if (string1 == null && (stringList.contains(null) || stringList.contains("null")))
            return new VerificationResultModel(true, "Null is one of the expected value.");
        return verifyConditionAndReturn(stringList.contains(string1),
                "'" + stringList + "' contains string '" + string1 + "'",
                "'" + stringList + "' does not contain string '" + string1 + "'");
    }

    public static VerificationResultModel isValueInIs(String value, Map values, String expectedValue) {
        boolean isValueExist = values.get(value) != null;
        if (!isValueExist)
            return new VerificationResultModel(false, "Node " + value + " does not exist in the key value provided.");
        String actualString = values.get(value).toString().trim();
        return verifyConditionAndReturn(actualString.equals(expectedValue.trim()),
                value + " is verified correctly. It is " + expectedValue,
                "Actual:" + actualString + " But expected:" + expectedValue.trim());
    }

    public static VerificationResultModel isValueInIsNot(String value, Map values, String expectedValue) {
        String actualString = values.get(value).toString().trim();
        return verifyConditionAndReturn(!actualString.equals(expectedValue.trim()),
                value + " is verified. Actual: " + actualString + " and it was not expected to be " + expectedValue
                , "Actual values for " + value + " is same as the passed value. " + expectedValue + " They were not expected to be same.");
    }

    public static VerificationResultModel verifyJsons(Response response, String expectedResponseString) {
        String actualResponseString = response.getBody().asString();
        try {
            JSONCompareResult compareResult = JSONCompare.compareJSON(expectedResponseString, actualResponseString,
                    JSONCompareMode.NON_EXTENSIBLE);
            return verifyConditionAndReturn(compareResult.passed(), "Expected and actual json responses are equal.",
                    "Following nodes failed in the json object - :" + compareResult.getMessage());
        } catch (Exception ex) {
            return new VerificationResultModel(false, "Exception occurred : " + ex.getMessage());
        }
    }

    public static VerificationResultModel downloadFromUrlAndSaveAs(String url, String fileName) {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(fileName));
        } catch (IOException ex) {
            return new VerificationResultModel(false, url + " could not be saved. " + ex.getMessage());
        }
        return new VerificationResultModel(true, url + " successfully saved with filename " + fileName);
    }

    public static VerificationResultModel verifyResponseFor(Response response, Map<String, String> valueToVerify) {
        Set<String> keys = valueToVerify.keySet();
        boolean overallResult = true;
        int counter = 0;
        Map<Integer, VerificationResultModel> verificationResultModelMap = new HashMap<>();
        for (String eachKey : keys) {
            VerificationResultModel eachVerificationResultModel = verifyInResponse(response, eachKey, valueToVerify.get(eachKey));
            verificationResultModelMap.put(counter++, eachVerificationResultModel);
            overallResult &= eachVerificationResultModel.getOverallResult();
        }
        if (overallResult)
            return new VerificationResultModel(true, verificationResultModelMap);
        return new VerificationResultModel(false, verificationResultModelMap);
    }

    public static VerificationResultModel verifyInResponse(Response response, String node, String expectedValue) {
        if (response == null || response.getBody() == null) {
            String message = node + " verification : Either apiResponse or apiResponse body was not found." + System.lineSeparator();
            return new VerificationResultModel(false, message);
        }
        if (expectedValue.trim().equalsIgnoreCase("exists")) {
            return verifyConditionAndReturn(doesNodeExist(response, node),
                    "Verified node " + node + " successfully. It exists in the actual apiResponse.",
                    "Node " + node + " was expected to exist but it does not.");
        }
        if (expectedValue.trim().toLowerCase().startsWith("doesnotexist")) {
            return verifyConditionAndReturn(!doesNodeExist(response, node),
                    "Verified node " + node + " successfully. It does not exist in the actual apiResponse.",
                    "Node " + node + " was expected not to exist but it does.");
        }
        String responseBody = response.getBody().asString();
        String actualValue = from(responseBody).getString(node);
        if (expectedValue.toLowerCase().trim().contentEquals("null")) {
            return verifyConditionAndReturn(actualValue == null,
                    "Verified node " + node + " successfully. It was null.",
                    "Node " + node + " was expected to be null but it was found to be " + actualValue);
        }
        if (expectedValue.toLowerCase().trim().contentEquals("not null")) {
            return verifyConditionAndReturn(actualValue != null,
                    node + " is verified to be not null. It was " + actualValue,
                    "The apiResponse node " + node + " either does not exist or is null. Expected : not null.");
        }
        if (expectedValue.toLowerCase().trim().contentEquals("empty object")) {
            return verifyConditionAndReturn(actualValue.startsWith("[:]"),
                    "The apiResponse node " + node + " is empty.",
                    node + " is verified to be not empty. It was " + actualValue);
        }
        if (expectedValue.trim().startsWith("shouldNotContain")) {
            return verifyConditionAndReturn(!actualValue.contains(expectedValue.replace("shouldNotContain", "")),
                    "Verified " + node + ". It was expected to fail.",
                    "Verified " + node + ". It should not have contained \"" + actualValue + "\" but it has this string.");
        }
        if (actualValue == null) {
            return new VerificationResultModel(false, "Could not get value for the node " + node);
        }
        return verifyConditionAndReturn(actualValue.contentEquals(expectedValue.trim()),
                "Successfully verified " + node + ". It was :\"" + actualValue + "\"",
                node + " verification failed. Actual: \"" + actualValue + "\" Expected: \"" + expectedValue + "\"");
    }

    public static VerificationResultModel verifyObjectNodeCount(Response response, String node, String count) {
        List<Object> actualValue = from(response.getBody().print()).getJsonObject(node);
        if (actualValue == null) {
            return new VerificationResultModel(false, "Could not find " + node + " in response.");
        }
        int expectedCount;
        try {
            expectedCount = Integer.parseInt(count.substring(1));
        } catch (NumberFormatException ex) {
            expectedCount = Integer.parseInt(count);
            count = "=" + count;
        }
        boolean isPassed;
        if (count.startsWith(">")) {
            isPassed = actualValue.size() > expectedCount;
        } else if (count.startsWith("<")) {
            isPassed = actualValue.size() < expectedCount;
        } else if (count.startsWith("=")) {
            isPassed = actualValue.size() == expectedCount;
        } else {
            return new VerificationResultModel(false, "Please get the condition as >1 or <2 or =3");
        }
        return verifyConditionAndReturn(isPassed,
                node + " items verification passed. It was " + count,
                node + " items verification failed. It was " + actualValue.size() + " but expected" + count);
    }

    public static VerificationResultModel compareIs(String actualValueString, String expectedCountString) {
        if (expectedCountString.contains("not null")) {
            if (String.valueOf(actualValueString).trim().equalsIgnoreCase("null"))
                return new VerificationResultModel(false, actualValueString + " is not null.");
            return new VerificationResultModel(true, actualValueString + " is verified to be null.");
        }
        if (expectedCountString.contains("null")) {
            if (String.valueOf(actualValueString).trim().equalsIgnoreCase("null"))
                return new VerificationResultModel(true, actualValueString + " is verified to be null.");
            return new VerificationResultModel(false, actualValueString + " is not null.");
        }
        if (expectedCountString.trim().length() == 0 || expectedCountString.trim().equalsIgnoreCase("not empty")) {
            if (String.valueOf(actualValueString).trim().length() == 0)
                return new VerificationResultModel(false, actualValueString + " is verified to be not empty.");
            return new VerificationResultModel(true, actualValueString + " is verified to be empty.");
        }
        if (expectedCountString.trim().length() == 0 || expectedCountString.trim().equalsIgnoreCase("empty")) {
            if (String.valueOf(actualValueString).trim().length() == 0)
                return new VerificationResultModel(true, actualValueString + " is verified to be empty.");
            return new VerificationResultModel(false, actualValueString + " is verified to be not empty.");
        }
        try {
            boolean isPassed;
            expectedCountString = (expectedCountString.startsWith("length")) ? expectedCountString.split("length")[1] : expectedCountString;
            int expectedCount = Integer.parseInt(expectedCountString.trim().substring(1));
            int actualValue = Integer.parseInt(actualValueString);

            if (expectedCountString.startsWith(">")) {
                isPassed = actualValue > expectedCount;
            } else if (expectedCountString.startsWith("<")) {
                isPassed = actualValue < expectedCount;
            } else if (expectedCountString.startsWith("=")) {
                isPassed = actualValue == expectedCount;
            } else {
                throw new Exception("Please get the condition as >1 or <2 or =3");
            }
            return new VerificationResultModel(isPassed, "Verified " + actualValueString + " and " + expectedCountString);
        } catch (Exception ex) {
            return new VerificationResultModel(false, "Could not determine type of comparison");
        }
    }

    private static boolean doesNodeExist(Response response, String node) {
        try {
            response.jsonPath().getByte(node);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static VerificationResultModel verifyConditionAndReturn(boolean successCondition, String successMessage, String failureMessage) {
        String message = successCondition ? successMessage : failureMessage;
        return new VerificationResultModel(successCondition, message);
    }
}
